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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "userConfig", havingValue = "exampleUserConfig")
    public ExampleUserConfiguration userConfiguration() {
        LOGGER.info("Example User Configuration Instantiated");
        return new ExampleUserConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "userWarmer", havingValue = "exampleUserCacheWarmer")
    public ExampleUserCacheWarmerFactory userCacheWarmerFactory() {
        LOGGER.info("Example User Cache Warmer Instantiated");
        return new ExampleUserCacheWarmerFactory();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "policyConfig", havingValue = "examplePolicyConfig")
    public ExamplePolicyConfiguration policyConfiguration() {
        LOGGER.info("Example Policy Configuration Instantiated");
        return new ExamplePolicyConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "policyWarmer", havingValue = "examplePolicyCacheWarmer")
    public ExamplePolicyCacheWarmerFactory policyCacheWarmerFactory() {
        LOGGER.info("Example Policy Cache Warmer Instantiated");
        return new ExamplePolicyCacheWarmerFactory();
    }
}
