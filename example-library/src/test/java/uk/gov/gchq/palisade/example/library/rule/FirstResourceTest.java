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

package uk.gov.gchq.palisade.example.library.rule;

import org.junit.jupiter.api.Test;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.user.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class FirstResourceTest {

    private static final User TEST_USER_HR = new User().userId("1").roles(Collections.singleton(Role.HR.name()));
    private static final User TEST_USER_NOT_HR = new User().userId("1").roles(Collections.singleton("Not HR"));
    private static final Context TEST_CONTEXT = new Context().purpose("purpose");
    private static final FileResource TEST_RESOURCE = new FileResource();
    private static final String FILE_ID_1 = "file1.avro";
    private static final String FILE_ID_2 = "file.other.file2.avro";
    private static final FirstResourceRule RESOURCE_RULE = new FirstResourceRule();

    @Test
    void hrGetFirstFile() {
        //Given - FileId, User
        TEST_RESOURCE.setId(FILE_ID_1);

        //When
        Resource actual = RESOURCE_RULE.apply(TEST_RESOURCE, TEST_USER_HR, TEST_CONTEXT);

        //Then
        assertThat(actual)
                .as("HR should be able to access first resource")
                .isEqualTo(TEST_RESOURCE);
    }

    @Test
    void nonHrGetFirstFile() {
        //Given - FileId, User
        TEST_RESOURCE.setId(FILE_ID_1);

        //When
        Resource actual = RESOURCE_RULE.apply(TEST_RESOURCE, TEST_USER_NOT_HR, TEST_CONTEXT);

        //Then
        assertThat(actual)
                .as("non-HR should not be able to access first resource")
                .isNull();
    }

    @Test
    void hrGetSecondFile() {
        //Given - FileId, User
        TEST_RESOURCE.setId(FILE_ID_2);

        //When
        Resource actual = RESOURCE_RULE.apply(TEST_RESOURCE, TEST_USER_HR, TEST_CONTEXT);

        //Then
        assertThat(actual)
                .as("HR should be able to access second resource")
                .isEqualTo(TEST_RESOURCE);
    }

    @Test
    void nonHrGetSecondFile() {
        //Given - FileId, User
        TEST_RESOURCE.setId(FILE_ID_2);

        //When
        Resource actual = RESOURCE_RULE.apply(TEST_RESOURCE, TEST_USER_NOT_HR, TEST_CONTEXT);

        //Then
        assertThat(actual)
                .as("non-HR should be able to access second resource")
                .isEqualTo(TEST_RESOURCE);
    }
}
