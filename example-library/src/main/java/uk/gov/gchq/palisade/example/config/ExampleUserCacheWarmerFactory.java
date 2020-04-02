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
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.common.ExampleUser;
import uk.gov.gchq.palisade.example.common.TrainingCourse;
import uk.gov.gchq.palisade.service.UserCacheWarmerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties
public class ExampleUserCacheWarmerFactory implements UserCacheWarmerFactory {

    private String userId = "";
    private Set<String> auths = Collections.emptySet();
    private Set<String> roles = Collections.emptySet();
    private EnumSet<TrainingCourse> trainingCourses = EnumSet.noneOf(TrainingCourse.class);

    /**
     * Constructor with 0 arguments for an example implementation
     * of the {@link UserCacheWarmerFactory} interface
     */
    public ExampleUserCacheWarmerFactory() {
    }

    /**
     * Constructor with 4 arguments for an example implementation
     * of the {@link UserCacheWarmerFactory} interface
     *
     * @param userId            a {@link String} value of a user.
     * @param auths             a {@link Set} of {@link String} auth values for the user.
     * @param roles             a {@link Set} of {@link String} role values for the user.
     * @param trainingCourses   an {@link EnumSet} of {@link TrainingCourse}s for the user.
     */
    public ExampleUserCacheWarmerFactory(final String userId, final Set<String> auths, final Set<String> roles, final EnumSet<TrainingCourse> trainingCourses) {
        this.userId = userId;
        this.auths = auths;
        this.roles = roles;
        this.trainingCourses = trainingCourses;
    }

    @Generated
    public String getUserId() {
        return userId;
    }

    @Generated
    public void setUserId(final String userId) {
        requireNonNull(userId);
        this.userId = userId;
    }

    @Generated
    public Set<String> getAuths() {
        return auths;
    }

    @Generated
    public void setAuths(final Set<String> auths) {
        requireNonNull(auths);
        this.auths = auths;
    }

    @Generated
    public Set<String> getRoles() {
        return roles;
    }

    @Generated
    public void setRoles(final Set<String> roles) {
        requireNonNull(roles);
        this.roles = roles;
    }

    @Generated
    public EnumSet<TrainingCourse> getTrainingCourses() {
        return trainingCourses;
    }

    @Generated
    public void setTrainingCourses(final String... trainingCourse) {
        requireNonNull(trainingCourse);
        for (String course : trainingCourse) {
            trainingCourses.add(TrainingCourse.valueOf(course));
        }
    }

    @Override
    public User userWarm() {
        return new ExampleUser()
                .trainingCompleted(trainingCourses)
                .userId(this.getUserId())
                .auths(this.getAuths())
                .roles(this.getRoles());
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExampleUserCacheWarmerFactory)) {
            return false;
        }
        final ExampleUserCacheWarmerFactory that = (ExampleUserCacheWarmerFactory) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(auths, that.auths) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(trainingCourses, that.trainingCourses);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(userId, auths, roles, trainingCourses);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExampleUserCacheWarmerFactory.class.getSimpleName() + "[", "]")
                .add("userId='" + userId + "'")
                .add("auths=" + auths)
                .add("roles=" + roles)
                .add("trainingCourses=" + trainingCourses)
                .add(super.toString())
                .toString();
    }
}
