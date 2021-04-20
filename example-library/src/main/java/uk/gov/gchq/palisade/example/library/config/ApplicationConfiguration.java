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

package uk.gov.gchq.palisade.example.library.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.service.user.config.UserConfiguration;
import uk.gov.gchq.palisade.service.user.config.UserPrepopulationFactory;

@Configuration
@ConditionalOnClass(UserConfiguration.class)
public class ApplicationConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "population", name = "userProvider", havingValue = "example")
    @ConfigurationProperties(prefix = "population")
    public UserConfiguration userConfiguration() {
        return new ExampleUserConfiguration();
    }

    @Bean
    @ConditionalOnProperty(prefix = "population", name = "userProvider", havingValue = "example")
    public UserPrepopulationFactory userPrepopulationFactory() {
        return new ExampleUserPrepopulationFactory();
    }
}
