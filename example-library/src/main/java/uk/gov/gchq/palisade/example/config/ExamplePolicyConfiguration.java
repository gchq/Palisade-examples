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
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.service.PolicyConfiguration;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties(prefix = "population")
public class ExamplePolicyConfiguration implements PolicyConfiguration {

    private String resource;
    private List<ExamplePolicyCacheWarmerFactory> policies;
    private List<ExampleUserCacheWarmerFactory> users;

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link PolicyConfiguration} interface
     */
    public ExamplePolicyConfiguration() {
        resource = "";
        policies = Collections.emptyList();
        users = Collections.emptyList();
    }

    /**
     * Constructor with 2 arguments for an example implementation
     * of the {@link PolicyConfiguration} interface
     *
     * @param resource  a {@link String} value of the resource that will have the policies applied to it.
     * @param policies  a {@link List} of objects implementing the {@link uk.gov.gchq.palisade.service.PolicyCacheWarmerFactory} interface
     * @param users     a {@link List} of objects implementing the {@link uk.gov.gchq.palisade.service.UserCacheWarmerFactory} interface
     */
    public ExamplePolicyConfiguration(final String resource, final List<ExamplePolicyCacheWarmerFactory> policies,
                                      final List<ExampleUserCacheWarmerFactory> users) {
        this.resource = resource;
        this.policies = policies;
        this.users = users;
    }

    @Override
    @Generated
    public List<ExamplePolicyCacheWarmerFactory> getPolicies() {
        return policies;
    }

    @Generated
    public void setPolicies(final List<ExamplePolicyCacheWarmerFactory> policies) {
        requireNonNull(policies);
        this.policies = policies;
    }

    @Generated
    public String getResource() {
        return resource;
    }

    @Generated
    public void setResource(final String resource) {
        requireNonNull(resource);
        this.resource = resource;
    }

    @Override
    @Generated
    public List<ExampleUserCacheWarmerFactory> getUsers() {
        return users;
    }

    @Generated
    public void setUsers(final List<ExampleUserCacheWarmerFactory> users) {
        requireNonNull(users);
        this.users = users;
    }

    private String createFilePath(final String file) {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(file);
        this.resource = absoluteFileURI.toString();
        System.out.println("Resource: " + resource);
        return resource;
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
        return Objects.equals(resource, that.resource) &&
                Objects.equals(policies, that.policies) &&
                Objects.equals(users, that.users);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(resource, policies, users);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExamplePolicyConfiguration.class.getSimpleName() + "[", "]")
                .add("resource='" + resource + "'")
                .add("policies=" + policies)
                .add("users=" + users)
                .add(super.toString())
                .toString();
    }
}
