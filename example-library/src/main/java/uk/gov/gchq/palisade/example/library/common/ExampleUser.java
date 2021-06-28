/*
 * Copyright 2018-2021 Crown Copyright
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
package uk.gov.gchq.palisade.example.library.common;

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.user.User;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

/**
 * An example user class that extends the {@link User} to include the training courses
 */
public class ExampleUser extends User {
    private static final long serialVersionUID = 1L;

    private final Set<TrainingCourse> trainingCourses = EnumSet.noneOf(TrainingCourse.class);

    /**
     * Default constructor
     */
    public ExampleUser() {
        //no-args constructor
    }

    /**
     * Replaces the completed training courses to the {@link ExampleUser}
     *
     * @param trainingCourses a {@link Set} of the completed training courses
     * @return the {@link ExampleUser} including the added training courses
     */
    public ExampleUser trainingCourses(final Set<TrainingCourse> trainingCourses) {
        requireNonNull(trainingCourses, "cannot add null training completed");
        this.trainingCourses.clear();
        this.trainingCourses.addAll(Collections.unmodifiableSet(trainingCourses));
        return this;
    }

    /**
     * Get the set of training courses for the {@link ExampleUser}
     *
     * @return the {@link EnumSet} of training courses
     */
    public Set<TrainingCourse> getTrainingCourses() {
        return Set.copyOf(trainingCourses);
    }

    /**
     * Set the training courses for the {@link ExampleUser}
     *
     * @param trainingCourses a set of training courses to be added
     */
    public void setTrainingCourses(final Set<TrainingCourse> trainingCourses) {
        trainingCourses(trainingCourses);
    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExampleUser)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final ExampleUser user = (ExampleUser) o;
        return Objects.equals(trainingCourses, user.trainingCourses);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), trainingCourses);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ExampleUser.class.getSimpleName() + "[", "]")
                .add("trainingCourses=" + trainingCourses)
                .add(super.toString())
                .toString();
    }
}
