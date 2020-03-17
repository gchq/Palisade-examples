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

import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.common.ExampleUser;
import uk.gov.gchq.palisade.example.common.TrainingCourse;
import uk.gov.gchq.palisade.service.CacheWarmerFactory;

import java.util.Collections;
import java.util.Set;

public class ExampleUserCacheWarmerFactory implements CacheWarmerFactory {

    private String userId;
    private Set<String> auths;
    private Set<String> roles;
    private TrainingCourse[] trainingCourses;

    public ExampleUserCacheWarmerFactory() {
        this.userId = "";
        this.auths = Collections.emptySet();
        this.roles = Collections.emptySet();
    }

    public ExampleUserCacheWarmerFactory(final String userId, final Set<String> auths, final Set<String> roles, final TrainingCourse... trainingCourses) {
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
        return trainingCourses;
    }

    public void setTrainingCourses(final TrainingCourse... trainingCourses) {
        this.trainingCourses = trainingCourses;
    }

    @Override
    public User warm() {
        return new ExampleUser()
                .trainingCompleted(this.getTrainingCourses())
                .userId(this.getUserId())
                .auths(this.getAuths())
                .roles(this.getRoles());
    }
}
