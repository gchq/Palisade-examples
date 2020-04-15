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

import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.request.AddResourceRequest;
import uk.gov.gchq.palisade.example.request.AddSerialiserRequest;
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
import uk.gov.gchq.palisade.service.SimpleConnectionDetail;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Convenience class for the examples to configure the users and data access policies for the example.
 */
public class ExampleConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);

    private Path file;

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
        this.file = Path.of(file);
        return this;
    }

    public void run(final String filename) {
        this.file(filename).initialiseExample();
    }

    public void initialiseExample() {
        addResources();
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

    private void addResources() {
        // Add the resource to the Resource-service
        LOGGER.info("ADDING RESOURCES");

        FileResource resource = createFileResource(file);

        for (ServiceInstance dataService : getServiceInstances("data-service")) {
            resource.setConnectionDetail(new SimpleConnectionDetail().uri(dataService.getUri().toString()));
            AddResourceRequest addResourceRequest = new AddResourceRequest()
                    .resource(resource);

            for (ServiceInstance resourceService : getServiceInstances("resource-service")) {
                resourceClient.addResource(resourceService.getUri(), addResourceRequest);
                LOGGER.info("Added resource {} to service {}", addResourceRequest, resourceService.getUri());
            }
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

    static FileResource createFileResource(final Path path) {
        FileResource file = fileResource(path).serialisedFormat("avro").type("employee");
        resolveParents(path.getParent(), file);

        return file;
    }

    private static void resolveParents(final Path path, final ChildResource childResource) {
        requireNonNull(path);
        Optional<Path> parent = Optional.ofNullable(path.getParent());
        parent.ifPresentOrElse(parentPath -> {
            DirectoryResource directoryResource = directoryResource(parentPath);
            childResource.setParent(directoryResource);
            resolveParents(parentPath, directoryResource);
        }, () -> {
            SystemResource systemResource = systemResource(path);
            childResource.setParent(systemResource);
        });
    }

    private static FileResource fileResource(final Path path) {
        return new FileResource().id(toURI(path).toString());
    }

    private static DirectoryResource directoryResource(final Path path) {
        return new DirectoryResource().id(toURI(path).toString());
    }

    private static SystemResource systemResource(final Path path) {
        return new SystemResource().id(toURI(path).toString());
    }

    private static URI toURI(final Path path) {
        return ExampleFileUtil.convertToFileURI(path.toString());
    }
}
