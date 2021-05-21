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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SimpleConnectionDetail;
import uk.gov.gchq.palisade.service.resource.config.ApplicationConfiguration;
import uk.gov.gchq.palisade.service.resource.config.R2dbcConfiguration;
import uk.gov.gchq.palisade.service.resource.config.ResourceConfiguration;
import uk.gov.gchq.palisade.service.resource.config.ResourcePrepopulationFactory;
import uk.gov.gchq.palisade.service.resource.config.StdResourceConfiguration;
import uk.gov.gchq.palisade.service.resource.config.StdResourcePrepopulationFactory;
import uk.gov.gchq.palisade.service.resource.stream.config.AkkaSystemConfig;

import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ActiveProfiles({"example-k8s"})
@DataR2dbcTest
@ContextConfiguration(classes = {ApplicationConfiguration.class, R2dbcConfiguration.class, AkkaSystemConfig.class})
@EntityScan(basePackages = {"uk.gov.gchq.palisade.service.resource.domain"})
@EnableR2dbcRepositories(basePackages = {"uk.gov.gchq.palisade.service.resource.repository"})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class ResourceServicePrepopTest {
    @Autowired
    ResourceConfiguration resourceConfiguration;

    @Test
    void testContextLoads() {
        // Given

        // When
        ResourcePrepopulationFactory prepopulationFactory = resourceConfiguration.getResources().get(0);
        var resourceEntry = prepopulationFactory.build(conn -> new SimpleConnectionDetail().serviceName(conn));

        // Then
        assertThat(resourceConfiguration)
                .as("Check the instance of the userConfiguration")
                .isInstanceOf(StdResourceConfiguration.class);

        assertThat(prepopulationFactory)
                .as("Check the instance of the prepopulationFactory")
                .isInstanceOf(StdResourcePrepopulationFactory.class);

        assertThat(resourceEntry.getValue())
                .as("Check the instance of the created user")
                .isInstanceOf(FileResource.class);

        var leafResourceIds = resourceConfiguration.getResources().stream()
                .map(factory -> factory.build(conn -> new SimpleConnectionDetail().serviceName(conn)))
                .map(Entry::getValue)
                .map(Resource::getId)
                .collect(Collectors.toSet());
        assertThat(leafResourceIds)
                .isEqualTo(Set.of(
                        "file:/data/local-data-store/data/employee_file0.avro",
                        "file:/data/local-data-store/data/employee_file1.avro"
                ));

        var rootResourceIds = resourceConfiguration.getResources().stream()
                .map(factory -> factory.build(conn -> new SimpleConnectionDetail().serviceName(conn)))
                .map(Entry::getKey)
                .map(Resource::getId)
                .collect(Collectors.toSet());
        assertThat(rootResourceIds)
                .isEqualTo(Set.of(
                        "file:/data/local-data-store/"
                ));
    }
}
