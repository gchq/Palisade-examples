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

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.service.PolicyConfiguration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uk.gov.gchq.palisade.example.common.ExamplePolicies.getParent;

@ConfigurationProperties(prefix = "population")
public class ExamplePolicyConfiguration implements PolicyConfiguration {

    private String resource;
    private List<ExamplePolicyCacheWarmerFactory> policies = new ArrayList<>();

    public ExamplePolicyConfiguration() {
    }

    public ExamplePolicyConfiguration(final List<ExamplePolicyCacheWarmerFactory> policies, final String resource) {
        this.policies = policies;
        this.resource = resource;
    }

    @Override
    public List<ExamplePolicyCacheWarmerFactory> getPolicies() {
        return policies;
    }

    public void setPolicies(final List<ExamplePolicyCacheWarmerFactory> policies) {
        this.policies = policies;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(final String resource) {
        this.resource = resource;
    }

    @Override
    public Resource createResource() {
        URI normalised = ExampleFileUtil.convertToFileURI(resource);
        String resource = normalised.toString();
        if (resource.endsWith(".avro")) {
            return new FileResource().id(resource).type(Employee.class.getTypeName()).serialisedFormat("avro").parent(getParent(resource));
        } else {
            return new DirectoryResource().id(resource).parent(getParent(resource));
        }
    }

    private String createFilePath(final String file) {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(file);
        this.resource = absoluteFileURI.toString();
        System.out.println("Resource: " + resource);
        return resource;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ExamplePolicyConfiguration that = (ExamplePolicyConfiguration) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(policies, that.policies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, policies);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExamplePolicyConfiguration{\n");
        sb.append("\tresource=").append(resource).append("\n");
        sb.append("\tpolicies=").append(policies).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
