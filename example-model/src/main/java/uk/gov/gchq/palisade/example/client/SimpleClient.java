/*
 * Copyright 2020 Crown Copyright
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.request.ReadRequest;
import uk.gov.gchq.palisade.example.request.RegisterDataRequest;
import uk.gov.gchq.palisade.example.web.DataClient;
import uk.gov.gchq.palisade.example.web.PalisadeClient;
import uk.gov.gchq.palisade.resource.LeafResource;
import uk.gov.gchq.palisade.service.ConnectionDetail;
import uk.gov.gchq.palisade.service.request.DataRequestResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class SimpleClient<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);
    private final Serialiser<T> serialiser;

    final PalisadeClient palisadeClient;
    final DataClient dataClient;

    final EurekaClient eurekaClient;

    public SimpleClient(final Serialiser<T> serialiser, final PalisadeClient palisadeClient, final DataClient dataClient, final EurekaClient eurekaClient) {
        this.serialiser = serialiser;
        this.palisadeClient = palisadeClient;
        this.dataClient = dataClient;
        this.eurekaClient = eurekaClient;
    }

    public Stream<T> read(final String filename, final String resourceType, final String userId, final String purpose) throws IOException, URISyntaxException {
        DataRequestResponse dataRequestResponse = makeRequest(filename, resourceType, userId, purpose);
        Stream<T> objectStreams = getObjectStreams(dataRequestResponse);
        return objectStreams;
    }

    private DataRequestResponse makeRequest(final String fileName, final String resourceType, final String userId, final String purpose) {
        RegisterDataRequest dataRequest = new RegisterDataRequest().resourceId(fileName).userId(new UserId().id(userId)).context(new Context().purpose(purpose));

        // While there may be many palisade services, just use one
        ServiceInstance palisadeService = getServiceInstances("palisade-service").get(0);
        return palisadeClient.registerDataRequestSync(palisadeService.getUri(), dataRequest);
    }

    public Stream<T> getObjectStreams(final DataRequestResponse response) throws URISyntaxException, IOException {
        requireNonNull(response, "response");

        final List<Stream<T>> dataStreams = new ArrayList<>(response.getResources().size());
        for (final Entry<LeafResource, ConnectionDetail> entry : response.getResources().entrySet()) {
            final ConnectionDetail connectionDetail = entry.getValue();
            final URI dataService = new URI(connectionDetail.createConnection());
            final RequestId uuid = response.getOriginalRequestId();

            final ReadRequest readRequest = new ReadRequest()
                    .token(response.getToken())
                    .resource(entry.getKey());
            readRequest.setOriginalRequestId(uuid);

            InputStream responseStream;
            try {
                LOGGER.info("HttpRequest");
                String requestString = new ObjectMapper().writeValueAsString(readRequest);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .POST(BodyPublishers.ofString(requestString))
                        .uri(URI.create(dataService.toString() + "/read/chunked"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/octet-stream")
                        .build();
                responseStream = HttpClient.newBuilder().version(Version.HTTP_2).build().sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                        .thenApply(HttpResponse::body).join();
            } catch (Exception ex) {
                LOGGER.error("{}:\n{}", ex.getMessage(), ex.getStackTrace());
                LOGGER.info("Feign");
                StreamingResponseBody responseBody = dataClient.readChunked(dataService, readRequest).getBody();
                responseStream = new PipedInputStream();
                PipedOutputStream internalSink = new PipedOutputStream((PipedInputStream) responseStream);
                responseBody.writeTo(internalSink);
                internalSink.close();
            }

            Stream<T> dataStream = getSerialiser().deserialise(responseStream);
            dataStreams.add(dataStream);
        }
        return dataStreams.stream().flatMap(Function.identity());
    }

    public Serialiser<T> getSerialiser() {
        return serialiser;
    }


    public List<ServiceInstance> getServiceInstances(final String serviceName) {
        return eurekaClient.getApplications().getRegisteredApplications().stream()
                .map(Application::getInstances)
                .flatMap(List::stream)
                .filter(instance -> instance.getAppName().equalsIgnoreCase(serviceName))
                .peek(instance -> LOGGER.info("Discovered {} :: {}:{}/{} ({})", instance.getAppName(), instance.getIPAddr(), instance.getPort(), instance.getSecurePort(), instance.getStatus()))
                .map(EurekaServiceInstance::new)
                .collect(Collectors.toList());
    }

}
