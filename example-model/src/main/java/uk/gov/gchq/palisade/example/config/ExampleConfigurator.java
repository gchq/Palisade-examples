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
package uk.gov.gchq.palisade.example.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.example.common.ExamplePolicies;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.service.palisade.service.PalisadeService;
import uk.gov.gchq.palisade.service.policy.request.SetResourcePolicyRequest;
import uk.gov.gchq.palisade.service.policy.service.PolicyService;
import uk.gov.gchq.palisade.service.request.Request;
import uk.gov.gchq.palisade.service.user.request.AddUserRequest;
import uk.gov.gchq.palisade.service.user.service.UserService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Convenience class for the examples to configure the users and data access policies for the example.
 */
public final class ExampleConfigurator {

    private static final String LOCALHOST = "http://localhost:";
    private static final String POLICY_PORT = "8085/";
    private static final String USER_PORT = "8087/";

    @Autowired
    private PalisadeService palisadeService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);
    private final String file;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();

    /**
     * Establishes policies and details for the examples and writes these into the configuration service.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        new ExampleConfigurator(args[0]);
    }

    public ExampleConfigurator(final String file) {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(file);
        this.file = absoluteFileURI.toString();
        initialiseExample();
    }

    private void initialiseExample() {
        // The user authorisation owner or sys admin needs to add the user

        final CompletableFuture<Boolean> userAliceStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getAlice())
        );

        final CompletableFuture<Boolean> userBobStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getBob())
        );

        final CompletableFuture<Boolean> userEveStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getEve())
        );

        // Using Custom Rule implementations
        final SetResourcePolicyRequest customPolicies = ExamplePolicies.getExamplePolicy(file);

        final CompletableFuture<Boolean> policyStatus = addPolicyRequest(customPolicies);

        // Wait for the users and policies to be loaded
        CompletableFuture.allOf(userAliceStatus, userBobStatus, userEveStatus, policyStatus).join();
        LOGGER.info("The example users and data access policies have been initialised.");
    }

    private CompletableFuture<Boolean> addUserRequest(final AddUserRequest request) {
        LOGGER.info("Adding user {} to the user service", request.user.getUserId().getId());
        String requestString = requestToString(request);
        LOGGER.info("Sending request to the user service");
        LOGGER.info("Add User Request: {}", request);
        URI uri = URI.create(LOCALHOST + USER_PORT + "addUser");
        LOGGER.info(uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("");

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private CompletableFuture<Boolean> addPolicyRequest(final SetResourcePolicyRequest request) {
        LOGGER.info("Adding resource policies to the policy service");
        String requestString = requestToString(request);
        LOGGER.info("Sending request to the policy service");
        URI uri = URI.create(LOCALHOST + POLICY_PORT + "setResourcePolicySync");
        LOGGER.info(uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .PUT(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private String requestToString(final Request request) {
        try {
            LOGGER.info("Parsing request to String");
            return this.mapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOGGER.info("Parsing request to String failed");
            throw new RuntimeException(ex);
        }
    }
}
