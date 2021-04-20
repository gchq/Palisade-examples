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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import uk.gov.gchq.palisade.example.library.config.ExampleUserPrepopulationFactory;
import uk.gov.gchq.palisade.service.user.UserApplication;
import uk.gov.gchq.palisade.service.user.config.UserPrepopulationFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = UserApplication.class, webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = {UserApplication.class})
@ActiveProfiles("example-k8s")
class ExampleUserServiceTest {
    @Autowired
    UserPrepopulationFactory prepopulationFactory;

    @Test
    void testContextLoads() {
        assertThat(prepopulationFactory).isInstanceOf(ExampleUserPrepopulationFactory.class);
    }
}
