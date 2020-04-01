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
import org.springframework.boot.context.properties.ConfigurationProperties;

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.service.PolicyCacheWarmerFactory;
import uk.gov.gchq.palisade.service.UserCacheWarmerFactory;
import uk.gov.gchq.palisade.service.request.Policy;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties
public class ExamplePolicyCacheWarmerFactory implements PolicyCacheWarmerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePolicyCacheWarmerFactory.class);

    private String type;
    private String owner;
    private Map<String, String> resourceRules;
    private Map<String, String> recordRules;

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link PolicyCacheWarmerFactory} interface
     */
    public ExamplePolicyCacheWarmerFactory() {
        type = "";
        owner = "";
        resourceRules = Collections.emptyMap();
        recordRules = Collections.emptyMap();
    }

    /**
     * Constructor with 4 arguments for an example implementation
     * of the {@link PolicyCacheWarmerFactory} interface
     *
     * @param type          a {@link String} of the {@link Policy} type.
     * @param owner         a {@link String} value of the owner of the policy
     * @param resourceRules a {@link Map} containing the ({@link String}) message and the ({@link String}) rule name.
     * @param recordRules   a {@link Map} containing the ({@link String}) message and the ({@link String}) rule name.
     */
    public ExamplePolicyCacheWarmerFactory(final String type, final String owner, final Map<String, String> resourceRules, final Map<String, String> recordRules) {
        this.type = type;
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
    public Policy<Employee> policyWarm(final List<? extends UserCacheWarmerFactory> users) {
        Policy<Employee> policy = new Policy<>();
        for (ExampleUserCacheWarmerFactory user : (List<ExampleUserCacheWarmerFactory>) users) {
            if (user.getUserId().equals(owner)) {
                policy.owner(user.userWarm());
            }
        }
        for (String key : resourceRules.keySet()) {
            policy.resourceLevelRule(key, (Rule<Resource>) createRule(resourceRules.get(key), "resource"));
        }
        for (String key : recordRules.keySet()) {
            policy.recordLevelRule(key, (Rule<Employee>) createRule(recordRules.get(key), "record"));
        }
        return policy;
    }

    private Rule<?> createRule(final String rule, final String ruleType) {
        if ("resource".equalsIgnoreCase(ruleType)) {
            try {
                LOGGER.debug("{} - {}", rule, ruleType);
                return (Rule<Resource>) Class.forName(rule).getConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                LOGGER.error("Error getting class: {}", ex.getMessage());
            } catch (IllegalAccessException e) {
                LOGGER.error("Error accessing constructor: {}", e.getMessage());
            } catch (InstantiationException e) {
                LOGGER.error("Error instantiating: {}", e.getMessage());
            } catch (InvocationTargetException e) {
                LOGGER.error("Invocation Target Exception: {}", e.getMessage());
            }
        }
        if ("record".equalsIgnoreCase(ruleType)) {
            try {
                LOGGER.debug("{} - {}", rule, ruleType);
                return (Rule<Employee>) Class.forName(rule).getConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                LOGGER.error("Error getting class: {}", ex.getMessage());
            } catch (IllegalAccessException e) {
                LOGGER.error("Error accessing constructor: {}", e.getMessage());
            } catch (InstantiationException e) {
                LOGGER.error("Error instantiating: {}", e.getMessage());
            } catch (InvocationTargetException e) {
                LOGGER.error("Invocation Target Exception: {}", e.getMessage());
            }
        }
        return null;
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
                Objects.equals(owner, that.owner) &&
                Objects.equals(resourceRules, that.resourceRules) &&
                Objects.equals(recordRules, that.recordRules);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(type, owner, resourceRules, recordRules);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExamplePolicyCacheWarmerFactory.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("owner='" + owner + "'")
                .add("resourceRules=" + resourceRules)
                .add("recordRules=" + recordRules)
                .add(super.toString())
                .toString();
    }
}
