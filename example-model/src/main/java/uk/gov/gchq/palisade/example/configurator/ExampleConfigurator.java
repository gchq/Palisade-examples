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
import uk.gov.gchq.palisade.example.request.AddSerialiserRequest;
import uk.gov.gchq.palisade.example.web.DataClient;
import uk.gov.gchq.palisade.reader.common.DataFlavour;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convenience class for the examples to configure the users and data access policies for the example.
 */
public class ExampleConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);

    private final DataClient dataClient;
    private final EurekaClient eurekaClient;

    public ExampleConfigurator(final DataClient dataClient, final EurekaClient eurekaClient) {
        this.dataClient = dataClient;
        this.eurekaClient = eurekaClient;
    }

    public void run() {
        this.initialiseExample();
    }

    public void initialiseExample() {
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

}
