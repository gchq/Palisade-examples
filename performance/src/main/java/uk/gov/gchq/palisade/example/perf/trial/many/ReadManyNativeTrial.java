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

package uk.gov.gchq.palisade.example.perf.trial.many;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;
import uk.gov.gchq.palisade.example.perf.util.PerfException;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * This test performs a native file read of many files in the 1st file set. This is done without going via Palisade, but
 * does try to deserialise the data.
 */
@Component
public class ReadManyNativeTrial extends PerfTrial {
    protected static final String NAME = "read_many_native";

    //create the serialiser
    private static final Serialiser<Employee> SERIALISER = new AvroSerialiser<>(Employee.class);

    /**
     * Default constructor
     */
    public ReadManyNativeTrial() {
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
        return "performs a native read and deserialise of many files";
    }

    /**
     * {@inheritDoc}
     */
    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        try (Stream<Path> manyFiles = Files.walk(Path.of(fileSet.manyDir))) {
            // collect all records of all resources into a single stream
            Stream<Stream<Employee>> data = manyFiles
                    .filter(path -> path.toFile().isFile())
                    .map((Path file) -> {
                        try {
                            //read from file
                            InputStream bis = Files.newInputStream(file);
                            return SERIALISER.deserialise(bis);
                        } catch (IOException ex) {
                            throw new PerfException(ex);
                        }
                    });
            //now read everything in each of the files
            nativeRead(data);
        } catch (IOException ex) {
            throw new PerfException(ex);
        }
    }
}
