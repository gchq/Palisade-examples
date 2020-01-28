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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class SimpleClient<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);
    private final Serialiser<T> serialiser;
    private static ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();

    public SimpleClient(final Serialiser<T> serialiser) {
        requireNonNull(serialiser, "serialiser cannot be null");
        this.serialiser = serialiser;
        mapper = JSONSerialiser.createDefaultMapper();
    }

    public Stream<T> read(final String filename, final String resourceType, final String userId, final String purpose) {
        LOGGER.info("");
        LOGGER.info("----------");
        final DataRequestResponse dataRequestResponse = createRequest(filename, resourceType, userId, purpose);
        LOGGER.info("----------");
        LOGGER.info("");
        return getObjectStreams(dataRequestResponse);
    }

    private DataRequestResponse createRequest(final String fileName, final String resourceType, final String userId, final String purpose) {
        final RegisterDataRequest dataRequest = new RegisterDataRequest().resourceId(fileName).userId(new UserId().id(userId)).context(new Context().purpose(purpose));
        LOGGER.info("");
        LOGGER.info("GETTING REQUEST CONFIG FROM PALISADE-SERVICE");
        LOGGER.info("");
        return postToPalisade(dataRequest);
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

    private DataRequestResponse postToPalisade(final RegisterDataRequest request) {

        DataRequestResponse dataRequestResponse = new DataRequestResponse();
        String requestString = requestToString(request);

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
        LOGGER.info("");
        LOGGER.info("Response received from Palisade-service");
        try {
            dataRequestResponse = mapper.readValue(responseString, DataRequestResponse.class);
        } catch (Exception ex) {
            LOGGER.error("Error mapping response: {}", ex.getMessage());
        }
        return dataRequestResponse;
    }

    private CompletableFuture<ReadResponse> postToDataService(final ReadRequest request, final String uri) {

        String requestString = requestToString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(URI.create(uri + "/read/chunked"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/octet-stream")
                .build();

        LOGGER.info("Http request created");
        LOGGER.info("Sending request to Data-service - {}", httpRequest.uri());
        LOGGER.info("");
        InputStream inputStream = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body).join();
        LOGGER.info(inputStream.toString());
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

}
