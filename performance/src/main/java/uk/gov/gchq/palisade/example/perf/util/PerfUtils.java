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

package uk.gov.gchq.palisade.example.perf.util;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;

import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;

/**
 * Utility methods for the performance tests.
 */
public class PerfUtils {

    public static final String WITH_POLICY_DIR = "with-policy";
    public static final String NO_POLICY_DIR = "no-policy";
    public static final String SMALL_FILE_NAME = "employee_small.avro";
    public static final String LARGE_FILE_NAME = "employee_large.avro";
    public static final String MANY_FILE_DIR = "many-employees";
    public static final String MANY_FILE_FORMAT = "employee_%05d.avro";

    private PerfUtils() {
    }

    public static Map.Entry<PerfFileSet, PerfFileSet> getFileSet(final Path directoryName) {
        return new SimpleImmutableEntry<>(
                new PerfFileSet(
                        directoryName.resolve(WITH_POLICY_DIR).resolve(SMALL_FILE_NAME),
                        directoryName.resolve(WITH_POLICY_DIR).resolve(LARGE_FILE_NAME),
                        directoryName.resolve(WITH_POLICY_DIR).resolve(MANY_FILE_DIR)),
                new PerfFileSet(
                        directoryName.resolve(NO_POLICY_DIR).resolve(SMALL_FILE_NAME),
                        directoryName.resolve(NO_POLICY_DIR).resolve(LARGE_FILE_NAME),
                        directoryName.resolve(NO_POLICY_DIR).resolve(MANY_FILE_DIR))
                );
    }
}
