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

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.service.PolicyPrepopulationFactory;
import uk.gov.gchq.palisade.service.PolicyConfiguration;
import uk.gov.gchq.palisade.service.UserPrepopulationFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties(prefix = "population")
public class ExamplePolicyConfiguration implements PolicyConfiguration {

    private List<ExamplePolicyPrepopulationFactory> policies;
    private List<ExampleUserPrepopulationFactory> users;

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link PolicyConfiguration} interface
     */
    public ExamplePolicyConfiguration() {
        policies = Collections.emptyList();
        users = Collections.emptyList();
    }

    /**
     * Constructor with 2 arguments for an example implementation
     * of the {@link PolicyConfiguration} interface
     *
     * @param policies  a {@link List} of objects implementing the {@link PolicyPrepopulationFactory} interface
     * @param users     a {@link List} of objects implementing the {@link UserPrepopulationFactory} interface
     */
    public ExamplePolicyConfiguration(final List<ExamplePolicyPrepopulationFactory> policies,
                                      final List<ExampleUserPrepopulationFactory> users) {
        this.policies = policies;
        this.users = users;
    }

    @Override
    @Generated
    public List<ExamplePolicyPrepopulationFactory> getPolicies() {
        return policies;
    }

    @Generated
    public void setPolicies(final List<ExamplePolicyPrepopulationFactory> policies) {
        requireNonNull(policies);
        this.policies = policies;
    }

    @Override
    @Generated
    public List<ExampleUserPrepopulationFactory> getUsers() {
        return users;
    }

    @Generated
    public void setUsers(final List<ExampleUserPrepopulationFactory> users) {
        requireNonNull(users);
        this.users = users;
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamplePolicyConfiguration)) {
            return false;
        }
        final ExamplePolicyConfiguration that = (ExamplePolicyConfiguration) o;
        return Objects.equals(policies, that.policies) &&
                Objects.equals(users, that.users);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(policies, users);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExamplePolicyConfiguration.class.getSimpleName() + "[", "]")
                .add("policies=" + policies)
                .add("users=" + users)
                .add(super.toString())
                .toString();
    }
}
