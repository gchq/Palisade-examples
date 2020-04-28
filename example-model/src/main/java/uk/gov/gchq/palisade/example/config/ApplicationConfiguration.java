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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uk.gov.gchq.palisade.clients.simpleclient.web.DataClient;
import uk.gov.gchq.palisade.clients.simpleclient.web.PalisadeClient;
import uk.gov.gchq.palisade.example.client.ExampleSimpleClient;
import uk.gov.gchq.palisade.example.configurator.ExampleConfigurator;
import uk.gov.gchq.palisade.example.runner.BulkTestExample;
import uk.gov.gchq.palisade.example.runner.RestExample;
import uk.gov.gchq.palisade.example.web.PolicyClient;
import uk.gov.gchq.palisade.example.web.ResourceClient;
import uk.gov.gchq.palisade.example.web.UserClient;
import uk.gov.gchq.palisade.jsonserialisation.JSONSerialiser;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableConfigurationProperties
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

    @Value("${example.directory:#{null}}")
    private String directory;
    @Value("${example.quantity:#{null}}")
    private Integer quantity;

    @ConditionalOnProperty(name = "example.type", havingValue = "bulk")
    @Bean("BulkRunner")
    public CommandLineRunner bulkRunner(final BulkTestExample bulkTestExample) {
        requireNonNull(directory, "--example.directory=... must be provided");
        requireNonNull(quantity, "--example.quantity=... must be provided");
        LOGGER.info("Constructed BulkRunner");
        return args -> {
            bulkTestExample.run(directory, quantity, true, true);
            System.exit(0);
        };
    }

    @Value("${example.userid:#{null}}")
    private String userId;
    @Value("${example.filename:#{null}}")
    private String filename;
    @Value("${example.purpose:#{null}}")
    private String purpose;

    @ConditionalOnProperty(name = "example.type", havingValue = "client", matchIfMissing = true)
    @Bean("ClientRunner")
    public CommandLineRunner clientRunner(final ExampleSimpleClient exampleClient) {
        requireNonNull(userId, "--example.userid=... must be provided");
        requireNonNull(filename, "--example.filename=... must be provided");
        requireNonNull(purpose, "--example.purpose=... must be provided");
        LOGGER.info("Constructed ClientRunner");
        return args -> {
            exampleClient.run(userId, filename, purpose);
            System.exit(0);
        };
    }

    @ConditionalOnProperty(name = "example.type", havingValue = "rest")
    @Bean("RestRunner")
    public CommandLineRunner restRunner(final RestExample restExample) {
        requireNonNull(filename, "--example.filename=... must be provided");
        LOGGER.info("Constructed RestRunner");
        return args -> {
            restExample.run(filename);
            System.exit(0);
        };
    }

    @ConditionalOnProperty(name = "example.type", havingValue = "configure")
    @Bean("ConfiguratorRunner")
    public CommandLineRunner configuratorRunner(final ExampleConfigurator configurator) {
        requireNonNull(filename, "--example.filename=... must be provided");
        LOGGER.info("Constructed ConfiguratorRunner");
        return args -> {
            configurator.run(filename);
            System.exit(0);
        };
    }

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        return JSONSerialiser.createDefaultMapper();
    }
}
