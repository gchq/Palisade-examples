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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.service.UserConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "population")
public class ExampleUserConfiguration implements UserConfiguration {

    @NestedConfigurationProperty
    private List<ExampleUserCacheWarmerFactory> users = new ArrayList<>();

    public ExampleUserConfiguration() {
    }

    public ExampleUserConfiguration(final List<ExampleUserCacheWarmerFactory> users) {
        this.users = users;
    }

    @Override
    public List<ExampleUserCacheWarmerFactory> getCacheWarmerFactory() {
        return users;
    }

    public void setCacheWarmerFactory(final List<ExampleUserCacheWarmerFactory> users) {
        this.users = users;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ExampleUserConfiguration that = (ExampleUserConfiguration) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }

    @Override
    public String toString() {
        return "ExampleUserConfiguration{" +
                "users=" + users +
                '}';
    }
}
