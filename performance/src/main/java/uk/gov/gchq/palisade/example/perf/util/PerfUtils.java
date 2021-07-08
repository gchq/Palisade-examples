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

package uk.gov.gchq.palisade.example.perf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;

import java.net.URI;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;

/**
 * Utility methods for the performance tests.
 */
public final class PerfUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerfUtils.class);
    public static final String WITH_POLICY_DIR = "with-policy";
    public static final String NO_POLICY_DIR = "no-policy";
    public static final String SMALL_FILE_NAME = "employee_small.avro";
    public static final String LARGE_FILE_NAME = "employee_large.avro";
    public static final String MANY_FILE_DIR = "many-employees";
    public static final String MANY_DUP_DIR_FORMAT = "duplicate_%06d";
    public static final String MANY_FILE_FORMAT = "employee_%06d.avro";

    /**
     * Hide constructor, this is a static class only
     */
    private PerfUtils() {
    }

    /**
     * For a given directory, get a pair of {@link PerfFileSet}s, the first for with-policy, second for no-policy
     * Each PerfFileSet is itself a triple of {@link Path}s, for the small-file, the large-file, then the many-directory
     *
     * @param directoryName the top-level directory for the performance-test dataset
     * @return a {@link Map.Entry} pair of {@link PerfFileSet} triples of {@link Path}s
     */
    public static Map.Entry<PerfFileSet, PerfFileSet> getFileSet(final Path directoryName) {
        return new SimpleImmutableEntry<>(
                new PerfFileSet(
                        getWithPolicyDir(directoryName).resolve(SMALL_FILE_NAME).toString(),
                        getWithPolicyDir(directoryName).resolve(LARGE_FILE_NAME).toString(),
                        getWithPolicyDir(directoryName).resolve(MANY_FILE_DIR).toString() + "/"),
                new PerfFileSet(
                        getNoPolicyDir(directoryName).resolve(SMALL_FILE_NAME).toString(),
                        getNoPolicyDir(directoryName).resolve(LARGE_FILE_NAME).toString(),
                        getNoPolicyDir(directoryName).resolve(MANY_FILE_DIR).toString() + "/")
        );
    }

    public static Path getDirectory(final String directoryName) {
        try {
            URI directoryUri = URI.create(directoryName);
            return Path.of(directoryUri);
        } catch (IllegalArgumentException ex) {
            String userDir = System.getProperty("user.dir") + "/";
            LOGGER.debug("Caught exception while creating URI", ex);
            LOGGER.debug("Suspect {} is a relative-path so trying with user.dir '{}' property", directoryName, userDir);
            URI directoryUri = Path.of(userDir + directoryName)
                    .toUri().normalize();
            return Path.of(directoryUri);
        }
    }

    /**
     * Get the directory that has policies
     *
     * @param directoryName the directory path
     * @return the {@link Path} for the directory with policies
     */
    public static Path getWithPolicyDir(final Path directoryName) {
        return directoryName.resolve(WITH_POLICY_DIR);
    }

    /**
     * Get the directory that has no policies
     *
     * @param directoryName the directory path
     * @return the {@link Path} for the directory with no policies
     */
    public static Path getNoPolicyDir(final Path directoryName) {
        return directoryName.resolve(NO_POLICY_DIR);
    }
}
