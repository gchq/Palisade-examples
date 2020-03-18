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

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.common.ExampleUser;
import uk.gov.gchq.palisade.example.common.TrainingCourse;
import uk.gov.gchq.palisade.service.CacheWarmerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@ConfigurationProperties
@EnableAutoConfiguration
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

    public TrainingCourse[] getTrainingCourses() {
        return (TrainingCourse[]) trainingCourses.toArray();
    }

    public void setTrainingCourses(final String... trainingCourse) {
        for (String course : trainingCourse) {
            trainingCourses.add(TrainingCourse.valueOf(course));
        }
    }

    @Override
    public User warm() {
        return new ExampleUser()
                .trainingCompleted(getTrainingCourses())
                .userId(this.getUserId())
                .auths(this.getAuths())
                .roles(this.getRoles());
    }
}
