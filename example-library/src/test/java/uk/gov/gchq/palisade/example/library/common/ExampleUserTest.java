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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import uk.gov.gchq.palisade.example.library.user.ExampleUser;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleUserTest {

    @Test
    void testDeserialiseExampleUser() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        // Given
        var user = new ExampleUser().trainingCompleted(TrainingCourse.PAYROLL_TRAINING_COURSE)
                .userId("bob")
                .roles(Role.HR.name(), "another_role")
                .auths("authorised_person", "more_authorisations");

        // When
        var actualJson = mapper.writeValueAsString(user);
        var actualInstance = mapper.readValue(actualJson, user.getClass());

        // Then
        assertThat(user)
                .as("Deserialised user should be equal to original user when using recursion")
                .usingRecursiveComparison()
                .isEqualTo(actualInstance);

        assertThat(user)
                .as("Deserialised user should be equal to original user")
                .isEqualTo(actualInstance);
    }
}
