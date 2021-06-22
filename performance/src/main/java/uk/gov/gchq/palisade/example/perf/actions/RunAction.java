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

package uk.gov.gchq.palisade.example.perf.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.example.perf.analysis.PerfCollector;
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.analysis.TrialType;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;
import uk.gov.gchq.palisade.example.perf.util.PerfUtils;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Runs a series of performance tests under various circumstances and then reports the metrics to a collector.
 */
public class RunAction implements Runnable {

    /**
     * Amount of time to wait between each trial.
     */
    public static final Duration TEST_DELAY = Duration.ofMillis(250);
    private static final Logger LOGGER = LoggerFactory.getLogger(RunAction.class);
    private static final String COLLECTOR = "collector";
    private final String directoryName;
    private final int dryRuns;
    private final int liveRuns;
    private final Map<String, PerfTrial> testsToRun;
    private final Set<String> skipTests;

    /**
     * Runner for executing a number of performance trials and collating statistical data on run-times
     *
     * @param directoryName top-level directory containing all further performance data to be requested frm palisade
     * @param dryRuns       number of dry-runs to perform, these are *not* counted in the final report
     * @param liveRuns      number of live-runs to perform, these are counted in the final report
     * @param trialsToRun   collection of different named trials to test
     * @param skipTests     names of trials to skip from the above collection
     */
    public RunAction(final String directoryName, final int dryRuns, final int liveRuns, final Map<String, PerfTrial> trialsToRun, final Set<String> skipTests) {
        this.directoryName = directoryName;
        this.dryRuns = dryRuns;
        this.liveRuns = liveRuns;
        this.testsToRun = trialsToRun;
        this.skipTests = skipTests;
    }

    /**
     * Run this action and throw a runtime exception if an error occurred
     *
     * @throws RuntimeException if an error occurred
     */
    public void run() {
        Path directory = PerfUtils.getDirectory(directoryName);
        Map.Entry<PerfFileSet, PerfFileSet> fileSet = PerfUtils.getFileSet(directory);

        // create the output collector
        PerfCollector collector = new PerfCollector();

        performTrialBatch(fileSet.getKey(), fileSet.getValue(), collector, skipTests);

        // write the performance test outputs
        collector.outputTo(LOGGER, buildNormalMap());
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

    /**
     * Perform a single batch of tests. This runs all tests the given number of times.
     *
     * @param fileSet     the file set for tests
     * @param noPolicySet the file set for tests with no policy
     * @param collector   the output collector
     * @param testsToSkip test names to skip
     * @throws IllegalArgumentException if any of {@code testsToSkip} are invalid
     * @throws IllegalArgumentException {@code trialCount} is less than 1
     */
    private void performTrialBatch(final PerfFileSet fileSet, final PerfFileSet noPolicySet, final PerfCollector collector, final Set<String> testsToSkip) {
        requireNonNull(collector, COLLECTOR);
        requireNonNull(testsToSkip, "testsToSkip");

        // iterate over each test to run and execute the given number of trials
        for (Map.Entry<String, PerfTrial> e : testsToRun.entrySet()) {
            // do we need to skip this one?
            if (testsToSkip.contains(e.getKey())) {
                LOGGER.info("Skipping test {}", e.getKey());
                continue;
            }

            // perform a number of dry and live runs of the trial
            LOGGER.info("Starting trial type {}", TrialType.DRY_RUN);
            performSingleTrial(dryRuns, e.getValue(), fileSet, noPolicySet, collector, TrialType.DRY_RUN);

            LOGGER.info("Starting trial type {}", TrialType.LIVE);
            performSingleTrial(liveRuns, e.getValue(), fileSet, noPolicySet, collector, TrialType.LIVE);
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
    private static void performSingleTrial(final int trialCount, final PerfTrial trial, final PerfFileSet fileSet, final PerfFileSet noPolicySet, final PerfCollector collector, final TrialType type) {
        requireNonNull(trial, "trial");
        requireNonNull(collector, COLLECTOR);
        if (trialCount < 1) {
            throw new IllegalArgumentException("trial count cannot be less than 1");
        }

        String trialName = trial.name();
        LOGGER.info("Starting test {}:", trialName);

        for (int i = 0; i < trialCount; i++) {
            delay(TEST_DELAY.toMillis());

            runTrial(trial, fileSet, noPolicySet, collector, type);
            LOGGER.info("Completed trial {}", i + 1);
        }

        LOGGER.info("Completed test {}", trialName);
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
    private static void runTrial(final PerfTrial trial, final PerfFileSet fileSet, final PerfFileSet noPolicySet, final PerfCollector collector, final TrialType type) {
        requireNonNull(trial, "trial");
        requireNonNull(collector, COLLECTOR);

        // perform trial
        try {
            trial.setup(fileSet, noPolicySet);
            long time = System.nanoTime();
            trial.runTrial(fileSet, noPolicySet);
            time = System.nanoTime() - time;
            trial.tearDown(fileSet, noPolicySet);
            // if this is a live trial then log it
            if (type == TrialType.LIVE) {
                collector.logTime(trial.name(), time);
            }
        } catch (Exception e) {
            LOGGER.warn("Performance test \"{}\" failed", trial.name());
            LOGGER.warn("Exception was :", e);
        }
    }

    /**
     * Create the map of test names to optional normal test names.
     *
     * @return normal map
     */
    private Map<String, String> buildNormalMap() {
        return testsToRun.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getNameForNormalisation()));
    }
}
