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

package uk.gov.gchq.palisade.example.perf.trial.small;

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
 * This test performs a native file read of small file in the 1st file set. This is done without going via Palisade, but
 * does try to deserialise the data.
 */
@Component
public class ReadSmallNativeTrial extends PerfTrial {
    protected static final String NAME = "read_small_native";

    //create the serialiser
    private static final Serialiser<Employee> SERIALISER = new AvroSerialiser<>(Employee.class);

    /**
     * Default constructor
     */
    public ReadSmallNativeTrial() {
        normal = NAME;
    }

    /**
     * Gets the name of the trial
     *
     * @return the name value of the trial
     */
    public String name() {
        return NAME;
    }

    /**
     * Gets the description of the trial
     *
     * @return the description value of the trial
     */
    public String description() {
        return "performs a native read and deserialise of the small file";
    }

    /**
     * Run the trial using the provided file sets
     *
     * @param fileSet     a collection of resources in a location that have a number of attached policies
     * @param noPolicySet a collection of resources in a location that do not have any attached policies in palisade
     */
    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        //read from file
        try (InputStream bis = Files.newInputStream(Path.of(fileSet.smallFile));
             Stream<Employee> dataStream = SERIALISER.deserialise(bis)) {

            //now read everything in the file
            nativeRead(Stream.of(dataStream));

        } catch (IOException e) {
            throw new PerfException(e);
        }
    }
}
