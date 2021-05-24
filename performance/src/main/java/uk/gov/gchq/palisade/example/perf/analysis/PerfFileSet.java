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

package uk.gov.gchq.palisade.example.perf.analysis;

import static java.util.Objects.requireNonNull;

/**
 * Simple class that contains details on the files being manipulated.
 * A triple of {@link String}s
 */
public class PerfFileSet {

    /**
     * Small file of data.
     */
    public final String smallFile;

    /**
     * Large file of data.
     */
    public final String largeFile;

    /**
     * Directory containing many small files of data
     */
    public final String manyDir;

    /**
     * Default constructor
     *
     * @param smallFile location of the 'small' file
     * @param largeFile location of the 'large' file
     * @param manyDir   location of the 'many' directory
     */
    public PerfFileSet(final String smallFile, final String largeFile, final String manyDir) {
        requireNonNull(smallFile, "smallFile");
        requireNonNull(largeFile, "largeFile");
        requireNonNull(manyDir, "manyDir");
        this.smallFile = smallFile;
        this.largeFile = largeFile;
        this.manyDir = manyDir;
    }
}
