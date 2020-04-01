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
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.resource.ParentResource;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SystemResource;
import uk.gov.gchq.palisade.service.PolicyConfiguration;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
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

    public ParentResource getParent(final String fileURL) {
        URI normalised = ExampleFileUtil.convertToFileURI(fileURL);
        //this should only be applied to URLs that start with 'file://' not other types of URL
        if (normalised.getScheme().equals(FileSystems.getDefault().provider().getScheme())) {
            Path current = Paths.get(normalised);
            Path parent = current.getParent();
            //no parent can be found, must already be a directory tree root
            if (isNull(parent)) {
                throw new IllegalArgumentException(fileURL + " is already a directory tree root");
            } else if (isDirectoryRoot(parent)) {
                //else if this is a directory tree root
                return new SystemResource().id(parent.toUri().toString());
            } else {
                //else recurse up a level
                return new DirectoryResource().id(parent.toUri().toString()).parent(getParent(parent.toUri().toString()));
            }
        } else {
            //if this is another scheme then there is no definable parent
            return new SystemResource().id("");
        }
    }

    public boolean isDirectoryRoot(final Path path) {
        return StreamSupport
                .stream(FileSystems.getDefault()
                        .getRootDirectories()
                        .spliterator(), false)
                .anyMatch(path::equals);
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
