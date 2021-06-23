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

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.service.user.config.UserConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of a {@link UserConfiguration} that uses Spring to configure a list of users from a yaml file
 * A container for a number of {@link ExampleUserConfiguration} builders used for creating {@link uk.gov.gchq.palisade.user.User}s
 * These users will be used for pre-populating the {@link uk.gov.gchq.palisade.service.user.service.UserService}
 */
public class ExampleUserConfiguration implements UserConfiguration {

    private List<ExampleUserPrepopulationFactory> users;

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link UserConfiguration} interface
     */
    public ExampleUserConfiguration() {
        users = Collections.emptyList();
    }

    /**
     * Constructor with 1 argument for an example implementation
     * of the {@link UserConfiguration} interface
     *
     * @param users a list of objects implementing the {@link uk.gov.gchq.palisade.service.user.config.UserPrepopulationFactory} interface
     */
    public ExampleUserConfiguration(final List<ExampleUserPrepopulationFactory> users) {
        this.users = List.copyOf(users);
    }

    @Override
    @Generated
    public List<ExampleUserPrepopulationFactory> getUsers() {
        return Collections.unmodifiableList(users);
    }

    @Generated
    public void setUsers(final List<ExampleUserPrepopulationFactory> users) {
        requireNonNull(users);
        this.users = List.copyOf(users);
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExampleUserConfiguration)) {
            return false;
        }
        final ExampleUserConfiguration that = (ExampleUserConfiguration) o;
        return Objects.equals(users, that.users);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(users);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExampleUserConfiguration.class.getSimpleName() + "[", "]")
                .add("users=" + users)
                .add(super.toString())
                .toString();
    }
}
