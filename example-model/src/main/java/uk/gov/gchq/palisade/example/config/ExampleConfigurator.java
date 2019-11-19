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
import uk.gov.gchq.palisade.service.user.request.AddUserRequest;
import uk.gov.gchq.palisade.service.user.service.UserService;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Convenience class for the examples to configure the users and data access policies for the example.
 */
public final class ExampleConfigurator {

    @Autowired
    private PalisadeService palisadeService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);
    private final String file;

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

        final CompletableFuture<Boolean> userAliceStatus = userService.addUser(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getAlice())
        );

        final CompletableFuture<Boolean> userBobStatus = userService.addUser(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getBob())
        );

        final CompletableFuture<Boolean> userEveStatus = userService.addUser(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getEve())
        );

        // Using Custom Rule implementations
        final SetResourcePolicyRequest customPolicies = ExamplePolicies.getExamplePolicy(file);

        final CompletableFuture<Boolean> policyStatus = policyService.setResourcePolicy(customPolicies);

        // Wait for the users and policies to be loaded
        CompletableFuture.allOf(userAliceStatus, userBobStatus, userEveStatus, policyStatus).join();
        LOGGER.info("The example users and data access policies have been initialised.");
    }
}
