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

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.service.PolicyConfiguration;
import uk.gov.gchq.palisade.service.PolicyPrepopulationFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

public class ExamplePolicyConfiguration implements PolicyConfiguration {

    private List<ExamplePolicyPrepopulationFactory> policies;

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link PolicyConfiguration} interface
     */
    public ExamplePolicyConfiguration() {
        policies = Collections.emptyList();
    }

    /**
     * Constructor with 1 argument for an example implementation
     * of the {@link PolicyConfiguration} interface
     *
     * @param policies  a {@link List} of objects implementing the {@link PolicyPrepopulationFactory} interface
     */
    public ExamplePolicyConfiguration(final List<ExamplePolicyPrepopulationFactory> policies) {
        this.policies = policies;
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamplePolicyConfiguration)) {
            return false;
        }
        final ExamplePolicyConfiguration that = (ExamplePolicyConfiguration) o;
        return Objects.equals(policies, that.policies);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(policies);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExamplePolicyConfiguration.class.getSimpleName() + "[", "]")
                .add("policies=" + policies)
                .add(super.toString())
                .toString();
    }
}
