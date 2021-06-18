/*
 * Copyright 2018-2021 Crown Copyright
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

package uk.gov.gchq.palisade.example.runner.config;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uk.gov.gchq.palisade.client.akka.AkkaClient;
import uk.gov.gchq.palisade.client.akka.AkkaClient.SSLMode;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.runner.runner.BulkTestExample;
import uk.gov.gchq.palisade.example.runner.runner.CommandLineExample;
import uk.gov.gchq.palisade.example.runner.runner.RestExample;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Map;

/**
 * Spring bean configuration to run the example
 */
@Configuration
@EnableConfigurationProperties
public class ApplicationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);


    @Bean
    ActorSystem actorSystem() {
        return ActorSystem.create("palisade-client");
    }

    @Bean
    AkkaClient client(final @Value("${web.client.palisade-service}") String palisadeService,
                      final @Value("${web.client.filtered-resource-service}") String filteredResourceService,
                      final @Value("${web.client}") Map<String, String> urlMap,
                      final ActorSystem actorSystem) {
        return new AkkaClient(palisadeService, filteredResourceService, urlMap, actorSystem, SSLMode.NONE);
    }

    @Bean
    AkkaClientWrapper<Employee> simpleClientWrapper(final AkkaClient client, final ActorSystem actorSystem) {
        return new AkkaClientWrapper<>(client, new AvroSerialiser<>(Employee.class), Materializer.createMaterializer(actorSystem));
    }

    @Bean
    @ConditionalOnProperty(name = "example.type", havingValue = "rest")
    @ConfigurationProperties(prefix = "example")
    RestConfiguration restExampleConfiguration() {
        return new RestConfiguration();
    }

    @Bean
    @ConditionalOnBean(RestConfiguration.class)
    CommandLineRunner restExample(final RestConfiguration configuration, final AkkaClientWrapper<Employee> client) {
        LOGGER.debug("Constructed RestExample");
        return new RestExample(configuration, client);
    }


    @Bean
    @ConditionalOnProperty(name = "example.type", havingValue = "bulk")
    @ConfigurationProperties(prefix = "example")
    BulkConfiguration bulkExampleConfiguration() {
        return new BulkConfiguration();
    }

    @Bean
    @ConditionalOnBean(BulkConfiguration.class)
    CommandLineRunner restExample(final BulkConfiguration configuration, final AkkaClientWrapper<Employee> client) {
        LOGGER.debug("Constructed RestExample");
        return new BulkTestExample(configuration, client);
    }

    @Bean
    @ConditionalOnProperty(name = "example.type", havingValue = "cli")
    @ConditionalOnMissingBean(CommandLineRunner.class)
    CommandLineRunner commandLineExample(final AkkaClientWrapper<Employee> client) {
        return new CommandLineExample(client);
    }

    @Bean
    @Primary
    ObjectMapper jacksonObjectMapper() {
        return new ObjectMapper();
    }
}
