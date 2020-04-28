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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.service.ConnectionDetail;

import java.util.function.Function;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "user", havingValue = "example")
    @ConfigurationProperties(prefix = "population")
    public ExampleUserConfiguration userConfiguration() {
        return new ExampleUserConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "user", havingValue = "example")
    public ExampleUserPrepopulationFactory userCacheWarmerFactory() {
        return new ExampleUserPrepopulationFactory();
    }

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "policy", havingValue = "example")
    @ConfigurationProperties(prefix = "population")
    public ExamplePolicyConfiguration policyConfiguration() {
        return new ExamplePolicyConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "policy", havingValue = "example")
    public ExamplePolicyPrepopulationFactory policyCacheWarmerFactory() {
        return new ExamplePolicyPrepopulationFactory();
    }

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "resource", havingValue = "std", matchIfMissing = true)
    @ConfigurationProperties(prefix = "population")
    public StdResourceConfiguration resourceConfiguration() {
        return new StdResourceConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "resource", havingValue = "std", matchIfMissing = true)
    public StdResourcePrepopulationFactory resourcePrepopulationFactory(final Function<String, ConnectionDetail> connectionDetailMapper) {
        return new StdResourcePrepopulationFactory(connectionDetailMapper);
    }
}
