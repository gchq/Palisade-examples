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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.runner.RestExample;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.resource.LeafResource;
import uk.gov.gchq.palisade.service.ConnectionDetail;
import uk.gov.gchq.palisade.service.palisade.config.ApplicationConfiguration;
import uk.gov.gchq.palisade.service.palisade.request.ReadRequest;
import uk.gov.gchq.palisade.service.palisade.request.ReadResponse;
import uk.gov.gchq.palisade.service.palisade.request.RegisterDataRequest;
import uk.gov.gchq.palisade.service.palisade.service.DataService;
import uk.gov.gchq.palisade.service.palisade.service.PalisadeService;
import uk.gov.gchq.palisade.service.request.DataRequestResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class ExampleSimpleClient<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExample.class);
    private static final String RESOURCE_TYPE = "employee";
    private static ApplicationConfiguration palisadeConfig = new ApplicationConfiguration();

    @Autowired
    private static PalisadeService palisadeService;
    private final Serialiser<T> serializer;


    public ExampleSimpleClient(final PalisadeService palisadeService) {
        requireNonNull(palisadeService, "palisade service must be provided");
        ExampleSimpleClient.palisadeService = palisadeService;
        this.serializer = (Serialiser<T>) new AvroSerialiser<>(Employee.class);
    }

    public static void main(final String[] args) {
        if (args.length == 3) {

            String userId = args[0];
            String filename = args[1];
            String purpose = args[2];
            User user = ExampleUsers.getUser(userId);
            LOGGER.info(user.getUserId().toString() + " is reading the Employee file with a purpose of " + purpose);
            final Stream<Employee> results = new ExampleSimpleClient(palisadeService).read(filename, user.getUserId().getId(), purpose);
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
        final DataRequestResponse dataRequestResponse = makeRequest(absoluteFile, RESOURCE_TYPE, userId, purpose);
        return getObjectStreams(dataRequestResponse);
    }

    private DataRequestResponse makeRequest(final String fileName, final String resourceType, final String userId, final String purpose) {
        final RegisterDataRequest dataRequest = new RegisterDataRequest().resourceId(fileName).userId(new UserId().id(userId)).context(new Context().purpose(purpose));
        return palisadeService.registerDataRequest(dataRequest).join();
    }

    public Serialiser<T> getSerializer() {
        return serializer;
    }

    public Stream<T> getObjectStreams(final DataRequestResponse response) {
        requireNonNull(response, "response");

        final List<CompletableFuture<Stream<T>>> futureResults = new ArrayList<>(response.getResources().size());
        for (final Entry<LeafResource, ConnectionDetail> entry : response.getResources().entrySet()) {
            final ConnectionDetail connectionDetail = entry.getValue();
            final DataService dataService = connectionDetail.createService();
            final RequestId uuid = response.getOriginalRequestId();

            final ReadRequest readRequest = new ReadRequest()
                    .token(response.getToken())
                    .resource(entry.getKey());
            readRequest.setOriginalRequestId(uuid);

            final CompletableFuture<ReadResponse> futureResponse = dataService.read(readRequest);
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

}
