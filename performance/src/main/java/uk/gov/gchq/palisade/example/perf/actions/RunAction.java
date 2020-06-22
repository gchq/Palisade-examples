/*
 * Copyright 2018 Crown Copyright
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

package uk.gov.gchq.palisade.example.perf.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.example.perf.trial.PerfCollector;
import uk.gov.gchq.palisade.example.perf.trial.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;
import uk.gov.gchq.palisade.example.perf.trial.TrialType;
import uk.gov.gchq.palisade.example.perf.util.PerfUtils;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.util.ResourceBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Runs a series of performance tests under various circumstances and then reports the metrics to a collector.
 */
public class RunAction implements IntSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunAction.class);

    /**
     * Amount of time to wait between each trial.
     */
    public static final Duration TEST_DELAY = Duration.ofMillis(250);

    private final String filename;
    private final int dryRuns;
    private final int liveRuns;
    private final Map<String, PerfTrial> testsToRun;
    private Set<String> skipTests;

    public RunAction(final String filename, final int dryRuns, final int liveRuns, final Map<String, PerfTrial> testsToRun, final Set<String> skipTests) {
        this.filename = filename;
        this.dryRuns = dryRuns;
        this.liveRuns = liveRuns;
        this.testsToRun = testsToRun;
        this.skipTests = skipTests;
    }

    public int getAsInt() {
        DirectoryResource directory = (DirectoryResource) ResourceBuilder.create(filename);

        Map.Entry<PerfFileSet, PerfFileSet> fileSet = PerfUtils.getFileSet(directory);

        // create the output collector
        PerfCollector collector = new PerfCollector();

        // do we need to do any dry runs?
        if (dryRuns > 0) {
            LOGGER.info("Starting dry runs");
            performTrialBatch(dryRuns, fileSet.getKey(), fileSet.getValue(), collector, TrialType.DRY_RUN, skipTests);
        }

        // do the live trials
        LOGGER.info("Starting live tests");
        performTrialBatch(liveRuns, fileSet.getKey(), fileSet.getValue(), collector, TrialType.LIVE, skipTests);

        // write the performance test outputs
        collector.outputTo(LOGGER, buildNormalMap());

        return 0;
    }

    /**
     * Create the map of test names to optional normal test names.
     *
     * @return normal map
     */
    private Map<String, Optional<String>> buildNormalMap() {
        return testsToRun.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getNameForNormalisation()));
    }

    /**
     * Perform a single batch of tests. This runs all tests the given number of times.
     *
     * @param trialCount  the number of trials of each test to run
     * @param fileSet     the file set for tests
     * @param noPolicySet the file set for tests with no policy
     * @param collector   the output collector
     * @param type        the test type being run
     * @param testsToSkip test names to skip
     * @throws IllegalArgumentException if any of {@code testsToSkip} are invalid
     * @throws IllegalArgumentException {@code trialCount} is less than 1
     */
    public void performTrialBatch(final int trialCount, final PerfFileSet fileSet, final PerfFileSet noPolicySet, final PerfCollector collector, final TrialType type, final Set<String> testsToSkip) {
        requireNonNull(collector, "collector");
        requireNonNull(testsToSkip, "testsToSkip");
        if (trialCount < 1) {
            throw new IllegalArgumentException("live trials cannot be less than 1");
        }

        // iterate over each test to run and execute the given number of trials
        for (Map.Entry<String, PerfTrial> e : testsToRun.entrySet()) {
            // do we need to skip this one?
            if (testsToSkip.contains(e.getKey())) {
                LOGGER.info("Skipping test {}", e.getKey());
                continue;
            }

            // perform a run of the named test
            performSingleTrial(trialCount, e.getValue(), fileSet, noPolicySet, collector, type);
        }
    }

    /**
     * Perform a single trial a given number of times.
     *
     * @param trialCount  the count
     * @param trial       the trial to run
     * @param fileSet     the file set for tests
     * @param noPolicySet the file set for tests with no policy
     * @param collector   the output collector
     * @param type        the type of test being run
     * @throws IllegalArgumentException {@code trialCount} is less than 1
     */
    public void performSingleTrial(final int trialCount, final PerfTrial trial, final PerfFileSet fileSet, final PerfFileSet noPolicySet, final PerfCollector collector, final TrialType type) {
        requireNonNull(trial, "trial");
        requireNonNull(collector, "collector");
        if (trialCount < 1) {
            throw new IllegalArgumentException("trial count cannot be less than 1");
        }

        LOGGER.info("Starting test {}:", trial.name());

        for (int i = 0; i < trialCount; i++) {
            delay(TEST_DELAY.toMillis());

            runTrial(trial, fileSet, noPolicySet, collector, type);
            LOGGER.info(".. {}", i + 1);
        }

        LOGGER.info(".. done");
    }

    /**
     * Perform a single run of a single trial.
     *
     * @param trial       the trial to run
     * @param fileSet     the file set for tests
     * @param noPolicySet the file set for tests with no policy
     * @param collector   the output collector
     * @param type        test type being run
     */
    public static void runTrial(final PerfTrial trial, final PerfFileSet fileSet, final PerfFileSet noPolicySet, final PerfCollector collector, final TrialType type) {
        requireNonNull(trial, "trial");
        requireNonNull(collector, "collector");

        // perform trial
        try {
            trial.setup(fileSet, noPolicySet);
            long time = System.nanoTime();
            trial.accept(fileSet, noPolicySet);
            time = System.nanoTime() - time;
            trial.tearDown(fileSet, noPolicySet);
            // if this is a live trial then log it
            if (type == TrialType.LIVE) {
                collector.logTime(trial.name(), time);
            }
        } catch (Exception e) {
            LOGGER.warn("Performance test \"{}\" failed because {}", trial.name(), e.getMessage());
        }
    }

    /**
     * Sleep method for separating runs.
     *
     * @param ms time to wait in milliseconds
     */
    private static void delay(final long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
