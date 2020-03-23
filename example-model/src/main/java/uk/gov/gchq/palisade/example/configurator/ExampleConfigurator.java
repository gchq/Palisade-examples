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
package uk.gov.gchq.palisade.example.configurator;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;

import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.common.ExamplePolicies;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.request.AddSerialiserRequest;
import uk.gov.gchq.palisade.example.request.AddUserRequest;
import uk.gov.gchq.palisade.example.request.SetResourcePolicyRequest;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.example.web.DataClient;
import uk.gov.gchq.palisade.example.web.PolicyClient;
import uk.gov.gchq.palisade.example.web.ResourceClient;
import uk.gov.gchq.palisade.example.web.UserClient;
import uk.gov.gchq.palisade.reader.common.DataFlavour;
import uk.gov.gchq.palisade.resource.ChildResource;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SystemResource;
import uk.gov.gchq.palisade.resource.request.AddResourceRequest;
import uk.gov.gchq.palisade.service.SimpleConnectionDetail;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Convenience class for the examples to configure the users and data access policies for the example.
 */
public class ExampleConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);

    private String file;

    private final DataClient dataClient;
    private final PolicyClient policyClient;
    private final ResourceClient resourceClient;
    private final UserClient userClient;

    private final EurekaClient eurekaClient;

    public ExampleConfigurator(final DataClient dataClient, final PolicyClient policyClient, final ResourceClient resourceClient, final UserClient userClient, final EurekaClient eurekaClient) {
        this.dataClient = dataClient;
        this.policyClient = policyClient;
        this.resourceClient = resourceClient;
        this.userClient = userClient;
        this.eurekaClient = eurekaClient;
    }

    public ExampleConfigurator file(final String file) {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(file);
        this.file = absoluteFileURI.toString();
        return this;
    }

    public void run(final String filename) {
        this.file(filename).initialiseExample();
    }

    public void initialiseExample() {
        addUsers();
        addResources();
        addPolicies();
        addSerialiser();

        LOGGER.info("The example users, data access policies, resource(s) and serialiser details have been initialised.");
    }

    public List<ServiceInstance> getServiceInstances(final String serviceName) {
        return eurekaClient.getApplications(serviceName).getRegisteredApplications().stream()
                .map(Application::getInstances)
                .flatMap(List::stream)
                .filter(instance -> instance.getAppName().equalsIgnoreCase(serviceName))
                .peek(instance -> LOGGER.info("Discovered {} :: {}:{}/{} ({})", instance.getAppName(), instance.getIPAddr(), instance.getPort(), instance.getSecurePort(), instance.getStatus()))
                .map(EurekaServiceInstance::new)
                .collect(Collectors.toList());
    }

    void addUsers() {
        // Add the users to the User-service
        LOGGER.info("ADDING USERS");

        AddUserRequest[] userRequests = new AddUserRequest[]{
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getAlice()),
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getBob()),
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getEve())
        };

        for (ServiceInstance userService : getServiceInstances("user-service")) {
            for (AddUserRequest addUserRequest : userRequests) {
                userClient.addUser(userService.getUri(), addUserRequest);
                LOGGER.info("Added user {} to service {}", addUserRequest, userService.getUri());
            }
        }

        LOGGER.info("");
    }

    void addResources() {
        // Add the resource to the Resource-service
        LOGGER.info("ADDING RESOURCES");

        FileResource resource = createFileResource(file);

        for (ServiceInstance dataService : getServiceInstances("data-service")) {
            AddResourceRequest addResourceRequest = new AddResourceRequest()
                    .resource(resource)
                    .connectionDetail(new SimpleConnectionDetail().uri(dataService.getUri().toString()));

            for (ServiceInstance resourceService : getServiceInstances("resource-service")) {
                resourceClient.addResource(resourceService.getUri(), addResourceRequest);
                LOGGER.info("Added resource {} to service {}", addResourceRequest, resourceService.getUri());
            }
        }

        LOGGER.info("");
    }

    void addPolicies() {
        // Using Custom Rule implementations
        LOGGER.info("ADDING POLICIES");

        SetResourcePolicyRequest setPolicyRequest = ExamplePolicies.getExamplePolicy(file);

        for (ServiceInstance policyService : getServiceInstances("policy-service")) {
            policyClient.setResourcePolicyAsync(policyService.getUri(), setPolicyRequest);
            LOGGER.info("Set policy {} to service {}", setPolicyRequest, policyService.getUri());
        }

        LOGGER.info("");
    }

    void addSerialiser() {
        // Add the Avro serialiser to the data-service
        LOGGER.info("ADDING SERIALISERS");

        AddSerialiserRequest addSerialiserRequest = new AddSerialiserRequest()
                .dataFlavour(DataFlavour.of("employee", "avro"))
                .serialiser(new AvroSerialiser<>(Employee.class));

        for (ServiceInstance dataService : getServiceInstances("data-service")) {
            dataClient.addSerialiser(dataService.getUri(), addSerialiserRequest);
            LOGGER.info("Added serialiser {} to service {}", addSerialiserRequest, dataService.getUri());
        }

        LOGGER.info("");
    }

    static FileResource createFileResource(final String id) {
        FileResource file = new FileResource().id(id).serialisedFormat("avro").type("employee");
        Path path = Path.of(id).toAbsolutePath();
        resolveParents(path.getParent(), file);

        return file;
    }

    private static void resolveParents(final Path path, final ChildResource childResource) {
        requireNonNull(path);
        Optional<Path> parent = Optional.ofNullable(path.getParent());
        parent.ifPresentOrElse(parentPath -> {
            DirectoryResource directoryResource = new DirectoryResource().id(path.toString());
            childResource.setParent(directoryResource);
            resolveParents(parentPath, directoryResource);
        }, () -> {
            SystemResource systemResource = new SystemResource().id(path.toString());
            childResource.setParent(systemResource);
        });
    }

}
