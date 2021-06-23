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

package uk.gov.gchq.palisade.example.perf.config;

import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.client.akka.AkkaClient;
import uk.gov.gchq.palisade.client.akka.AkkaClient.SSLMode;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.perf.actions.ActionRunner;
import uk.gov.gchq.palisade.example.perf.actions.CreateAction;
import uk.gov.gchq.palisade.example.perf.actions.RunAction;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bean configuration and dependency injection graph
 */
@Configuration
@EnableConfigurationProperties
public class ApplicationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    ActorSystem actorSystem() {
        return ActorSystem.create("palisade-client");
    }

    @Bean
    AkkaClient client(final @Value("${web.client.palisade-service}") String palisadeService,
                      final @Value("${web.client.filtered-resource-service}") String filteredResourceService,
                      final @Value("${web.client.data-service}") String dataService,
                      final ActorSystem actorSystem) {
        return new AkkaClient(palisadeService, filteredResourceService, Map.of("data-service", dataService), actorSystem, SSLMode.NONE);
    }

    @Bean
    AkkaClientWrapper<Employee> simpleClientWrapper(final AkkaClient client) {
        return new AkkaClientWrapper<>(client, new AvroSerialiser<>(Employee.class));
    }

    /**
     * Bean for PerformanceConfiguration containing a number of options for the performance tests
     *
     * @return the configuration, populated from yaml 'performance.*' field
     */
    @Bean
    @ConfigurationProperties(prefix = "performance")
    PerformanceConfiguration runConfiguration() {
        return new PerformanceConfiguration();
    }

    /**
     * Bean for a CommandLineRunner to use as the entrypoint for this application, configured to run a performance test
     *
     * @param conf         configuration for the conditions of the test - number of runs, data set size, directory locations etc.
     * @param perfTrialSet the set of trials to perform - these may be further overridden by the configuration's skipTests field
     * @return a SpringBootApplication CommandLineRunner that will run the trial set with the given configuration
     */
    @Bean
    @ConditionalOnProperty(name = "performance.action", havingValue = "run")
    CommandLineRunner runAction(final PerformanceConfiguration conf, final Collection<PerfTrial> perfTrialSet) {
        Map<String, PerfTrial> testsToRun = perfTrialSet.stream()
                .collect(Collectors.toMap(PerfTrial::name, Function.identity()));
        LOGGER.debug("Created RunAction with conf {} and tests {}", conf, testsToRun);
        return new ActionRunner(new RunAction(conf.getDirectory(), conf.getDryRuns(), conf.getLiveRuns(), testsToRun, new HashSet<>(conf.getSkipTests())));
    }

    /**
     * Bean for a CommandLineRunner to use as the entrypoint for this application, configured to create a set of test data before
     * performance tests can be run
     *
     * @param conf configuration for the conditions of the data set - size and directory location
     * @return a SpringBootApplication CommandLineRunner that will create the data set with the given configuration
     */
    @Bean
    @ConditionalOnProperty(name = "performance.action", havingValue = "create")
    CommandLineRunner createAction(final PerformanceConfiguration conf) {
        LOGGER.debug("Created CreateAction with conf {}", conf);
        return new ActionRunner(new CreateAction(conf.getDirectory(), conf.getSmall(), conf.getLarge(), conf.getManyUnique(), conf.getManyDuplicates()));
    }
}
