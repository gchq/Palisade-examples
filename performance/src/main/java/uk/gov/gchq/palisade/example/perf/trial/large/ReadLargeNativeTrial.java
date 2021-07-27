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

package uk.gov.gchq.palisade.example.perf.trial.large;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.AbstractPerfTrial;
import uk.gov.gchq.palisade.example.perf.util.PerfException;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * This test performs a native file read of large file in the 1st file set. This is done without going via Palisade, but
 * does try to deserialise the data.
 */
@Component
public class ReadLargeNativeTrial extends AbstractPerfTrial {
    protected static final String NAME = "read_large_native";
    private static final String DESCRIPTION = "performs a native read and deserialise of the large file";

    /**
     * Default constructor
     */
    public ReadLargeNativeTrial() {
        normal = NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String name() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String description() {
        return DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        //get file URI
        //read from file
        try (InputStream bis = Files.newInputStream(Path.of(fileSet.largeFile));
             Stream<Employee> dataStream = SERIALISER.deserialise(bis)) {

            //now read everything in the file
            nativeRead(Stream.of(dataStream));

        } catch (IOException e) {
            throw new PerfException(e);
        }
    }
}
