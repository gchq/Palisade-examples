/*
 * Copyright 2018 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.palisade.example.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.request.ClientReadResponse;
import uk.gov.gchq.palisade.example.request.ReadRequest;
import uk.gov.gchq.palisade.example.request.ReadResponse;
import uk.gov.gchq.palisade.example.request.RegisterDataRequest;
import uk.gov.gchq.palisade.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.palisade.resource.LeafResource;
import uk.gov.gchq.palisade.service.ConnectionDetail;
import uk.gov.gchq.palisade.service.request.DataRequestResponse;
import uk.gov.gchq.palisade.service.request.Request;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class SimpleClient<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);
    private static final String DISCOVERY_URL = "http://localhost:8083/";
    private final Serialiser<T> serialiser;
    private static ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();

    private List<ServiceInstance> palisadeServiceInstances;

    public SimpleClient(final Serialiser<T> serialiser) {
        requireNonNull(serialiser, "serialiser cannot be null");
        this.serialiser = serialiser;
        mapper = JSONSerialiser.createDefaultMapper();
        palisadeServiceInstances = getServiceInstances("palisade-service");
    }

    public Stream<T> read(final String filename, final String resourceType, final String userId, final String purpose) {
        LOGGER.info("");
        LOGGER.info("----------");
        final CompletableFuture<DataRequestResponse> dataRequestResponse = createRequest(filename, resourceType, userId, purpose);
        LOGGER.info("----------");
        LOGGER.info("");
        return getObjectStreams(dataRequestResponse.join());
    }

    private CompletableFuture<DataRequestResponse> createRequest(final String fileName, final String resourceType, final String userId, final String purpose) {
        final RegisterDataRequest dataRequest = new RegisterDataRequest().resourceId(fileName).userId(new UserId().id(userId)).context(new Context().purpose(purpose));
        LOGGER.info("");
        LOGGER.info("GETTING REQUEST CONFIG FROM PALISADE-SERVICE");
        LOGGER.info("");
        return postToPalisade(dataRequest, palisadeServiceInstances);
    }

    public Stream<T> getObjectStreams(final DataRequestResponse response) {
        requireNonNull(response, "response");

        final List<CompletableFuture<Stream<T>>> futureResults = new ArrayList<>(response.getResources().size());
        for (final Entry<LeafResource, ConnectionDetail> entry : response.getResources().entrySet()) {
            final ConnectionDetail connectionDetail = entry.getValue();
            final String url = connectionDetail.createConnection();
            final RequestId uuid = response.getOriginalRequestId();

            final ReadRequest readRequest = new ReadRequest()
                    .token(response.getToken())
                    .resource(entry.getKey());
            readRequest.setOriginalRequestId(uuid);

            LOGGER.info("READING RESOURCE FROM DATA-SERVICE");
            LOGGER.info("");
            final CompletableFuture<ReadResponse> futureResponse = postToDataService(readRequest, url);
            LOGGER.info("Response received from Data-service");
            LOGGER.info("");
            final CompletableFuture<Stream<T>> futureResult = futureResponse.thenApply(
                    dataResponse -> {
                        try {
                            return getSerialiser().deserialise(dataResponse.asInputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            futureResults.add(futureResult);
            LOGGER.info("----------");
            LOGGER.info("");
        }
        return futureResults.stream().flatMap(CompletableFuture::join);
    }

    private CompletableFuture<DataRequestResponse> postToPalisade(final RegisterDataRequest request, final List<ServiceInstance> instances) {

        DataRequestResponse dataRequestResponse = new DataRequestResponse();
        String requestString = requestToString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(URI.create(getServiceUri(instances) + "/registerDataRequest"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("Sending request to Palisade-service");
        String responseString = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();
        LOGGER.info("");
        LOGGER.info("Response received from Palisade-service");
        try {
            dataRequestResponse = mapper.readValue(responseString, DataRequestResponse.class);
        } catch (Exception ex) {
            LOGGER.error("Error mapping response: {}", ex.getMessage());
        }
        return CompletableFuture.completedFuture(dataRequestResponse);
    }

    private CompletableFuture<ReadResponse> postToDataService(final ReadRequest request, final String uri) {

        String requestString = requestToString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(URI.create(uri + "/read/chunked"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/octet-stream")
                .build();

        LOGGER.info("Sending request to Data-service");
        LOGGER.info("");
        InputStream inputStream = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body).join();

        ClientReadResponse readResponse = new ClientReadResponse(inputStream);
        return CompletableFuture.completedFuture(readResponse);
    }

    private String requestToString(final Request request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOGGER.info("Parsing request to String failed");
            throw new RuntimeException(ex);
        }
    }

    public Serialiser<T> getSerialiser() {
        return serialiser;
    }

    private List<ServiceInstance> getServiceInstances(final String name) {
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();

        URI uri = URI.create(DISCOVERY_URL + "service-instances/" + name);
        LOGGER.info("Requesting information from Eureka for {}", name);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        String strResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> String.valueOf(response.body())).join();

        List<String> stringList = processResponse(strResponse);
        for (String string : stringList) {
            try {
                InstanceInfo instanceInfo = mapper.readValue(string, InstanceInfo.class);
                ServiceInstance serviceInstance = new EurekaServiceInstance(instanceInfo);
                serviceInstanceList.add(serviceInstance);
            } catch (Exception ex) {
                LOGGER.error("Error occurred while processing response: {}", ex.getMessage());
            }
        }

        LOGGER.info("{} instances found: {}", name, serviceInstanceList.size());
        LOGGER.info("");
        return serviceInstanceList;
    }

    private List<String> processResponse(final String value) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(value);
        } catch (JSONException ex) {
            LOGGER.error("Error creating JSONArray: {}", ex.getMessage());
        }

        final JSONArray finalJsonArray = jsonArray;
        if (finalJsonArray != null) {
            return IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> ((JSONObject) finalJsonArray.get(index)).optString("instanceInfo")).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private String getServiceUri(final List<ServiceInstance> instances) {
        ServiceInstance instance = instances.get(0);
        if (instance.isSecure()) {
            return "https://" + instance.getHost() + ":" + instance.getMetadata().get("management.port");
        } else {
            return "http://" + instance.getHost() + ":" + instance.getMetadata().get("management.port");
        }
    }

}
