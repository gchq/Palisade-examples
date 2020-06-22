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

import uk.gov.gchq.palisade.example.perf.trial.PerfFileSet;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.util.ResourceBuilder;

import java.net.URI;
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

    private PerfUtils() {
    }

    public static FileResource getSmallFile(final DirectoryResource directoryResource) {
        return (FileResource) ResourceBuilder.create(URI.create(directoryResource.getId()).resolve(WITH_POLICY_DIR).resolve(SMALL_FILE_NAME));
    }

    public static FileResource getLargeFile(final DirectoryResource directoryResource) {
        return (FileResource) ResourceBuilder.create(URI.create(directoryResource.getId()).resolve(WITH_POLICY_DIR).resolve(LARGE_FILE_NAME));
    }

    public static FileResource getNoPolicyName(final FileResource fileResource) {
        return (FileResource) ResourceBuilder.create(fileResource.getId().replace(WITH_POLICY_DIR, NO_POLICY_DIR));
    }

    public static Map.Entry<PerfFileSet, PerfFileSet> getFileSet(DirectoryResource directoryResource) {
        return new SimpleImmutableEntry<>(new PerfFileSet(
                getSmallFile(directoryResource),
                getLargeFile(directoryResource)
        ), new PerfFileSet(
                getNoPolicyName(getSmallFile(directoryResource)),
                getNoPolicyName(getLargeFile(directoryResource))
        ));
    }
}
