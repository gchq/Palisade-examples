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

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.request.ClientReadResponse;
import uk.gov.gchq.palisade.example.request.ReadRequest;
import uk.gov.gchq.palisade.example.request.RegisterDataRequest;
import uk.gov.gchq.palisade.example.web.DataClient;
import uk.gov.gchq.palisade.example.web.PalisadeClient;
import uk.gov.gchq.palisade.resource.LeafResource;
import uk.gov.gchq.palisade.service.ConnectionDetail;
import uk.gov.gchq.palisade.service.request.DataRequestResponse;

import java.io.IOException;
import java.io.InputStream;
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
        DataRequestResponse registeredRequest = makeRequest(filename, resourceType, userId, purpose);
        Stream<T> objectStreams = getObjectStreams(registeredRequest);
        return objectStreams;
    }

    private DataRequestResponse makeRequest(final String fileName, final String resourceType, final String userId, final String purpose) {
        // While there may be many palisade services, just use one
        ServiceInstance palisadeService = getServiceInstances("palisade-service").get(0);

        RegisterDataRequest dataRequest = new RegisterDataRequest().resourceId(fileName).userId(new UserId().id(userId)).context(new Context().purpose(purpose));

        LOGGER.info("Registered data request: {}", dataRequest);
        DataRequestResponse dataResponse =  palisadeClient.registerDataRequestSync(palisadeService.getUri(), dataRequest);
        LOGGER.info("Received data response: {}", dataResponse);

        return dataResponse;
    }

    public Stream<T> getObjectStreams(final DataRequestResponse registeredRequest) throws URISyntaxException, IOException {
        requireNonNull(registeredRequest, "registeredRequest");

        final List<Stream<T>> dataStreams = new ArrayList<>(registeredRequest.getResources().size());
        for (final Entry<LeafResource, ConnectionDetail> entry : registeredRequest.getResources().entrySet()) {
            final ConnectionDetail connectionDetail = entry.getValue();
            final URI dataService = new URI(connectionDetail.createConnection());
            final RequestId uuid = registeredRequest.getOriginalRequestId();

            final ReadRequest readRequest = new ReadRequest()
                    .token(registeredRequest.getToken())
                    .resource(entry.getKey());
            readRequest.setOriginalRequestId(uuid);

            LOGGER.info("Requested data read: {}", readRequest);
            String requestString = new ObjectMapper().writeValueAsString(readRequest);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(BodyPublishers.ofString(requestString))
                    .uri(URI.create(dataService.toString() + "/read/chunked"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/octet-stream")
                    .build();

            LOGGER.info("Sending request to Data-service");
            LOGGER.info("");
            InputStream inputStream = HttpClient.newBuilder().version(Version.HTTP_2).build().sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                    .thenApply(HttpResponse::body).join();

            ClientReadResponse readResponse = new ClientReadResponse(inputStream);
            //StreamingResponseBody response = dataClient.readChunked(dataService, readRequest);

            //LOGGER.info("Sinking internal stream");
            //PipedInputStream inputStream = new PipedInputStream();
            //PipedOutputStream internalSink = new PipedOutputStream(inputStream);
            //ReadResponse readResponse = new ClientReadResponse(inputStream);

            //response.writeTo(internalSink);
            Stream<T> dataStream = getSerialiser().deserialise(readResponse.asInputStream());
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
