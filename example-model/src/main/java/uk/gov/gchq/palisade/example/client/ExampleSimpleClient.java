/*
 * Copyright 2019 Crown Copyright
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.request.ReadRequest;
import uk.gov.gchq.palisade.example.request.ReadResponse;
import uk.gov.gchq.palisade.example.request.RegisterDataRequest;
import uk.gov.gchq.palisade.example.runner.RestExample;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.palisade.resource.LeafResource;
import uk.gov.gchq.palisade.service.ConnectionDetail;
import uk.gov.gchq.palisade.service.request.DataRequestResponse;
import uk.gov.gchq.palisade.service.request.Request;

import java.io.IOException;
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
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class ExampleSimpleClient<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExample.class);
    private static final String RESOURCE_TYPE = "employee";
    private static ObjectMapper mapper = new ObjectMapper();
    private final Serialiser<T> serializer;
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();


    public ExampleSimpleClient() {
        this.serializer = (Serialiser<T>) new AvroSerialiser<>(Employee.class);
        mapper = JSONSerialiser.createDefaultMapper();
    }

    public static void main(final String[] args) {
        if (args.length == 3) {

            String userId = args[0];
            String filename = args[1];
            String purpose = args[2];
            User user = ExampleUsers.getUser(userId);
            LOGGER.info(String.format("%s is reading the Employee file %s with a purpose of %s", user.getUserId().toString(), filename, purpose));
            final Stream<Employee> results = new ExampleSimpleClient().read(filename, user.getUserId().getId(), purpose);
            LOGGER.info(user.getUserId().toString() + " got back: ");
            results.map(Object::toString).forEach(LOGGER::info);

        } else {
            System.out.printf("Usage: %s userId resource purpose\n\n", ExampleSimpleClient.class.getSimpleName());
            System.out.println("userId\t\t the unique id of the user making this query");
            System.out.println("resource\t the name of the resource being requested");
            System.out.println("purpose\t\t purpose for accessing the resource");
        }
    }

    public Stream<T> read(final String filename, final String userId, final String purpose) {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(filename);
        String absoluteFile = absoluteFileURI.toString();
        final DataRequestResponse dataRequestResponse = createRequest(absoluteFile, RESOURCE_TYPE, userId, purpose);
        return getObjectStreams(dataRequestResponse);
    }

    private DataRequestResponse createRequest(final String fileName, final String resourceType, final String userId, final String purpose) {
        final RegisterDataRequest dataRequest = new RegisterDataRequest().resourceId(fileName).userId(new UserId().id(userId)).context(new Context().purpose(purpose));
        LOGGER.info("Data Request: {}", dataRequest);
        return postToPalisade(dataRequest);
    }

    public Serialiser<T> getSerializer() {
        return serializer;
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

            final CompletableFuture<ReadResponse> futureResponse = postToDataService(readRequest, url);
            final CompletableFuture<Stream<T>> futureResult = futureResponse.thenApply(
                    dataResponse -> {
                        try {
                            return getSerializer().deserialise(dataResponse.asInputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            futureResults.add(futureResult);
        }
        return futureResults.stream().flatMap(CompletableFuture::join);
    }

    private DataRequestResponse postToPalisade(final RegisterDataRequest request) {

        DataRequestResponse dataRequestResponse = new DataRequestResponse();
        String requestString = requestToString(request);
        LOGGER.info("Parsing complete");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(URI.create("http://localhost:8084/registerDataRequest"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("Http request created");
        LOGGER.info("Sending request to Palisade-service - {}", httpRequest.uri());
        String responseString = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();
        LOGGER.info("Response received from Palisade-service");
        try {
            dataRequestResponse = mapper.readValue(responseString, DataRequestResponse.class);
        } catch (Exception ex) {
            LOGGER.error("Error mapping response: {}", ex.getMessage());
        }
        return dataRequestResponse;
    }

    private CompletableFuture<ReadResponse> postToDataService(final ReadRequest request, final String uri) {

        CompletableFuture<ReadResponse> futureResponse = new CompletableFuture<>();
        String requestString = requestToString(request);
        LOGGER.info(requestString);
        LOGGER.info("Parsing complete");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(URI.create(uri + "/read"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("Http request created");
        LOGGER.info("Sending request to Data-service - {}", httpRequest.uri());
        String responseString = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).join();
        try {
            futureResponse = CompletableFuture.completedFuture(mapper.readValue(responseString, ReadResponse.class));
        } catch (Exception ex) {
            LOGGER.error("Error mapping response: {}", ex.getMessage());
        }
        LOGGER.info("Response received from Data-service for resource {}", request.getResource().getId());
        return futureResponse;
    }

    private String requestToString(final Request request) {
        try {
            LOGGER.info("Parsing request to String");
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOGGER.info("Parsing request to String failed");
            throw new RuntimeException(ex);
        }
    }

}
