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

package uk.gov.gchq.palisade.example.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import uk.gov.gchq.palisade.example.library.common.ExampleUser;
import uk.gov.gchq.palisade.example.library.config.ExampleUserConfiguration;
import uk.gov.gchq.palisade.example.library.config.ExampleUserPrepopulationFactory;
import uk.gov.gchq.palisade.service.user.config.ApplicationConfiguration;
import uk.gov.gchq.palisade.service.user.config.UserConfiguration;
import uk.gov.gchq.palisade.service.user.config.UserPrepopulationFactory;
import uk.gov.gchq.palisade.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ApplicationConfiguration.class})
@EnableAutoConfiguration
@ActiveProfiles({"example-k8s"})
class ExampleUserServiceTest {
    @Autowired
    UserConfiguration userConfiguration;

    @Test
    void testContextLoads() {
        // Given

        // When
        UserPrepopulationFactory prepopulationFactory = userConfiguration.getUsers().get(0);
        User user = prepopulationFactory.build();

        // Then
        assertThat(userConfiguration)
                .as("Check the instance of the userConfiguration")
                .isInstanceOf(ExampleUserConfiguration.class);

        assertThat(prepopulationFactory)
                .as("Check the instance of the prepopulationFactory")
                .isInstanceOf(ExampleUserPrepopulationFactory.class);

        assertThat(user)
                .as("Check the instance of the created user")
                .isInstanceOf(ExampleUser.class);
    }
}
