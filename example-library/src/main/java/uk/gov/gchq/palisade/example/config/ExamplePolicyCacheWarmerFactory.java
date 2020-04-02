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

import org.apache.avro.reflect.MapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.resource.ParentResource;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SystemResource;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.service.PolicyCacheWarmerFactory;
import uk.gov.gchq.palisade.service.UserCacheWarmerFactory;
import uk.gov.gchq.palisade.service.request.Policy;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@ConfigurationProperties
public class ExamplePolicyCacheWarmerFactory implements PolicyCacheWarmerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePolicyCacheWarmerFactory.class);

    private String type = "";
    private String resource = "";
    private String owner = "";
    private Map<String, String> resourceRules = Collections.emptyMap();
    private Map<String, String> recordRules = Collections.emptyMap();

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link PolicyCacheWarmerFactory} interface
     */
    public ExamplePolicyCacheWarmerFactory() {
    }

    /**
     * Constructor with 5 arguments for an example implementation
     * of the {@link PolicyCacheWarmerFactory} interface
     *
     * @param type          a {@link String} value of the {@link Policy} type.
     * @param resource      a {@link String} value of the {@link Resource} to be used.
     * @param owner         a {@link String} value of the owner of the policy
     * @param resourceRules a {@link Map} containing the ({@link String}) message and the ({@link String}) rule name.
     * @param recordRules   a {@link Map} containing the ({@link String}) message and the ({@link String}) rule name.
     */
    public ExamplePolicyCacheWarmerFactory(final String type, final String resource, final String owner,
                                           final Map<String, String> resourceRules, final Map<String, String> recordRules) {
        this.type = type;
        this.resource = resource;
        this.owner = owner;
        this.resourceRules = resourceRules;
        this.recordRules = recordRules;
    }

    @Generated
    public String getType() {
        return type;
    }

    @Generated
    public void setType(final String type) {
        requireNonNull(type);
        this.type = type;
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

    @Generated
    public String getOwner() {
        return owner;
    }

    @Generated
    public void setOwner(final String owner) {
        requireNonNull(owner);
        this.owner = owner;
    }

    @Generated
    public Map<String, String> getResourceRules() {
        return resourceRules;
    }

    @Generated
    public void setResourceRules(final Map<String, String> resourceRules) {
        requireNonNull(resourceRules);
        this.resourceRules = resourceRules;
    }

    @Generated
    public Map<String, String> getRecordRules() {
        return recordRules;
    }

    @Generated
    public void setRecordRules(final Map<String, String> recordRules) {
        requireNonNull(recordRules);
        this.recordRules = recordRules;
    }

    @Override
    public Entry<Resource, Policy> policyWarm(final List<? extends UserCacheWarmerFactory> users) {
        Policy<Employee> policy = new Policy<>();
        for (ExampleUserCacheWarmerFactory user : (List<ExampleUserCacheWarmerFactory>) users) {
            if (user.getUserId().equals(owner)) {
                policy.owner(user.userWarm());
            }
        }
        for (Entry<String, String> entry : resourceRules.entrySet()) {
            policy.resourceLevelRule(entry.getKey(), (Rule<Resource>) createRule(entry.getValue(), "resource"));
        }
        for (Entry<String, String> entry : recordRules.entrySet()) {
            policy.recordLevelRule(entry.getKey(), (Rule<Employee>) createRule(entry.getValue(), "record"));
        }
        return new MapEntry<>(createResource(), policy);
    }

    private Rule<?> createRule(final String rule, final String ruleType) {
        if ("resource".equalsIgnoreCase(ruleType)) {
            try {
                LOGGER.debug("Adding rule {} for rule type {}", rule, ruleType);
                return (Rule<Resource>) Class.forName(rule).getConstructor().newInstance();
            } catch (Exception ex) {
                LOGGER.error("Error creating resourceLevel rule: {} - {}", ex.getMessage(), ex.getCause());
            }
        }
        if ("record".equalsIgnoreCase(ruleType)) {
            try {
                LOGGER.debug("Adding rule {} for rule type {}", rule, ruleType);
                return (Rule<Employee>) Class.forName(rule).getConstructor().newInstance();
            } catch (Exception ex) {
                LOGGER.error("Error creating recordLevel rule: {} - {}", ex.getMessage(), ex.getCause());
            }
        }
        return null;
    }

    @Override
    public Resource createResource() {
        URI normalised = ExampleFileUtil.convertToFileURI(resource);
        String resourceString = normalised.toString();
        if (resource.endsWith(".avro")) {
            return new FileResource().id(resourceString).type(Employee.class.getTypeName()).serialisedFormat("avro").parent(getParent(resourceString));
        } else {
            return new DirectoryResource().id(resourceString).parent(getParent(resourceString));
        }
    }

    private ParentResource getParent(final String fileURL) {
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

    private boolean isDirectoryRoot(final Path path) {
        return StreamSupport
                .stream(FileSystems.getDefault()
                        .getRootDirectories()
                        .spliterator(), false)
                .anyMatch(path::equals);
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamplePolicyCacheWarmerFactory)) {
            return false;
        }
        final ExamplePolicyCacheWarmerFactory that = (ExamplePolicyCacheWarmerFactory) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(resource, that.resource) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(resourceRules, that.resourceRules) &&
                Objects.equals(recordRules, that.recordRules);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(type, resource, owner, resourceRules, recordRules);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExamplePolicyCacheWarmerFactory.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("resource='" + resource + "'")
                .add("owner='" + owner + "'")
                .add("resourceRules=" + resourceRules)
                .add("recordRules=" + recordRules)
                .add(super.toString())
                .toString();
    }
}
