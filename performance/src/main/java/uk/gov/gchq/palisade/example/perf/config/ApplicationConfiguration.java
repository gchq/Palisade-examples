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

package uk.gov.gchq.palisade.example.perf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.model.client.ExampleSimpleClient;
import uk.gov.gchq.palisade.example.perf.actions.ActionRunner;
import uk.gov.gchq.palisade.example.perf.actions.CreateAction;
import uk.gov.gchq.palisade.example.perf.actions.RunAction;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;
import uk.gov.gchq.palisade.example.perf.trial.ReadLargeNativeTrial;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties
public class ApplicationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    public Function<String, Stream<Employee>> configuredSimpleClient(final ExampleSimpleClient client, final PerformanceConfiguration conf) {
        LOGGER.info("Configured ExampleSimpleClient with config {}", conf);
        return resourceId -> {
            try {
                return client.read(resourceId, conf.getUserId(), conf.getPurpose());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    @ConfigurationProperties(prefix = "performance")
    public PerformanceConfiguration runConfiguration() {
        return new PerformanceConfiguration();
    }

    @Bean
    @ConditionalOnProperty(name = "performance.action", havingValue = "run")
    public CommandLineRunner runAction(final PerformanceConfiguration conf, final Set<PerfTrial> perfTrialSet) {
        Map<String, PerfTrial> testsToRun = perfTrialSet.stream()
                .map(trial -> trial.setNameForNormalisation(new ReadLargeNativeTrial()))
                .collect(Collectors.toMap(PerfTrial::name, Function.identity()));
        LOGGER.info("Created RunAction with conf {} and tests {}", conf, testsToRun);
        return new ActionRunner(new RunAction(conf.getDirectory(), conf.getDryRuns(), conf.getLiveRuns(), testsToRun, new HashSet<>(conf.getSkipTests())));
    }

    @Bean
    @ConditionalOnProperty(name = "performance.action", havingValue = "create")
    public CommandLineRunner createAction(final PerformanceConfiguration conf) {
        LOGGER.info("Created CreateAction with conf {}", conf);
        return new ActionRunner(new CreateAction(conf.getDirectory(), conf.getSmall(), conf.getLarge()));
    }
}
