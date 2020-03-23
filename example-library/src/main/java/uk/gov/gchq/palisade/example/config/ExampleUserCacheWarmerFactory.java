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

import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.common.ExampleUser;
import uk.gov.gchq.palisade.example.common.TrainingCourse;
import uk.gov.gchq.palisade.service.CacheWarmerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@ConfigurationProperties
public class ExampleUserCacheWarmerFactory implements CacheWarmerFactory {

    private String userId;
    private Set<String> auths;
    private Set<String> roles;
    private EnumSet<TrainingCourse> trainingCourses;

    public ExampleUserCacheWarmerFactory() {
        this.userId = "";
        this.auths = Collections.emptySet();
        this.roles = Collections.emptySet();
        this.trainingCourses = EnumSet.noneOf(TrainingCourse.class);
    }

    public ExampleUserCacheWarmerFactory(final String userId, final Set<String> auths, final Set<String> roles, final EnumSet<TrainingCourse> trainingCourses) {
        this.userId = userId;
        this.auths = auths;
        this.roles = roles;
        this.trainingCourses = trainingCourses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public Set<String> getAuths() {
        return auths;
    }

    public void setAuths(final Set<String> auths) {
        this.auths = auths;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(final Set<String> roles) {
        this.roles = roles;
    }

    public EnumSet<TrainingCourse> getTrainingCourses() {
        return trainingCourses;
    }

    public void setTrainingCourses(final String... trainingCourse) {
        for (String course : trainingCourse) {
            trainingCourses.add(TrainingCourse.valueOf(course));
        }
    }

    @Override
    public User warm() {
        System.out.println("Using the ExampleUser warm method");
        return new ExampleUser()
                .trainingCompleted(trainingCourses)
                .userId(this.getUserId())
                .auths(this.getAuths())
                .roles(this.getRoles());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ExampleUserCacheWarmerFactory that = (ExampleUserCacheWarmerFactory) o;

        return Objects.equals(userId, that.userId) &&
                Objects.equals(auths, that.auths) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(trainingCourses, that.trainingCourses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, auths, roles, trainingCourses);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExampleUsers{\n");
        sb.append("\tuserId=").append(userId).append('\n');
        sb.append("\tauths=").append(auths).append('\n');
        sb.append("\troles=").append(roles).append('\n');
        sb.append("\ttrainingCourses=").append(trainingCourses).append("\n");
        sb.append('}');
        return sb.toString();
    }
}
