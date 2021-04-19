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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import uk.gov.gchq.palisade.user.User;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ExampleUserTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldDeserialiseExampleUser() throws IOException {
        //given
        User user = new ExampleUser().trainingCompleted(TrainingCourse.PAYROLL_TRAINING_COURSE).userId("bob").roles(Role.HR.name(), "another_role").auths("authorised_person", "more_authorisations");

        //when
        byte[] bytesSerialised = MAPPER.writeValueAsBytes(user);
        User newUser = MAPPER.readValue(bytesSerialised, ExampleUser.class);

        //then
        assertEquals("Deserialised user should be same class as original user", user.getClass(), newUser.getClass());
        assertEquals("Deserialised user should be equal to original user", user, newUser);
    }
}
