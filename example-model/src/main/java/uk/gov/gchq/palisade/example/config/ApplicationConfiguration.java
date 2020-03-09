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

package uk.gov.gchq.palisade.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uk.gov.gchq.palisade.example.client.ExampleSimpleClient;
import uk.gov.gchq.palisade.example.configurator.ExampleConfigurator;
import uk.gov.gchq.palisade.example.runner.BulkTestExample;
import uk.gov.gchq.palisade.example.runner.RestExample;
import uk.gov.gchq.palisade.example.web.DataClient;
import uk.gov.gchq.palisade.example.web.PalisadeClient;
import uk.gov.gchq.palisade.example.web.PolicyClient;
import uk.gov.gchq.palisade.example.web.ResourceClient;
import uk.gov.gchq.palisade.example.web.UserClient;
import uk.gov.gchq.palisade.jsonserialisation.JSONSerialiser;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
public class ApplicationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean("BulkExample")
    public BulkTestExample bulkExample(final RestExample client) {
        LOGGER.debug("Constructed BulkExample");
        return new BulkTestExample(client);
    }

    @Bean("RestExample")
    public RestExample restExample(final ExampleSimpleClient client) {
        LOGGER.debug("Constructed RestExample");
        return new RestExample(client);
    }

    @Bean("ExampleConfigurator")
    public ExampleConfigurator exampleConfigurator(final DataClient dataClient, final PolicyClient policyClient, final ResourceClient resourceClient, final UserClient userClient, final EurekaClient eurekaClient) {
        LOGGER.debug("Constructed ExampleConfigurator");
        return new ExampleConfigurator(dataClient, policyClient, resourceClient, userClient, eurekaClient);
    }

    @Bean("ExampleClient")
    public ExampleSimpleClient exampleClient(final PalisadeClient palisadeClient, final DataClient dataClient, final EurekaClient eurekaClient) {
        LOGGER.debug("Constructed ExampleClient");
        return new ExampleSimpleClient(palisadeClient, dataClient, eurekaClient);
    }

    @ConditionalOnProperty(name = "example", havingValue = "bulk")
    @Bean("BulkRunner")
    public CommandLineRunner bulkRunner(final BulkTestExample bulkTestExample) {
        LOGGER.info("Constructed BulkRunner");
        return bulkTestExample::run;
    }

    @ConditionalOnProperty(name = "example", havingValue = "rest")
    @Bean("RestRunner")
    public CommandLineRunner restRunner(final RestExample restExample) {
        LOGGER.info("Constructed RestRunner");
        return restExample::run;
    }

    @ConditionalOnProperty(name = "example", havingValue = "configure")
    @Bean("ConfiguratorRunner")
    public CommandLineRunner configuratorRunner(final ExampleConfigurator configurator) {
        LOGGER.info("Constructed ConfiguratorRunner");
        return configurator::run;
    }

    @ConditionalOnProperty(name = "example", havingValue = "client", matchIfMissing = true)
    @Bean("ClientRunner")
    public CommandLineRunner clientRunner(final ExampleSimpleClient exampleClient) {
        LOGGER.info("Constructed ClientRunner");
        return exampleClient::run;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSONSerialiser.createDefaultMapper();
    }
}
