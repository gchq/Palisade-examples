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

package uk.gov.gchq.palisade.example.perf.trial.large;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.stream.Stream;

/**
 * This test performs a native file read of large file in the 1st file set. This is done without going via Palisade, but
 * does try to deserialise the data.
 */
@Component
public class ReadLargeNativeTrial extends PerfTrial {
    protected static final String NAME = "read_large_native";
    //create the serialiser
    private static final Serialiser<Employee> SERIALISER = new AvroSerialiser<>(Employee.class);

    public ReadLargeNativeTrial() {
        normal = NAME;
    }

    public String name() {
        return NAME;
    }

    public String description() {
        return "performs a native read and deserialise of the large file";
    }

    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        //get file URI
        //read from file
        try (InputStream bis = Files.newInputStream(fileSet.largeFile);
             Stream<Employee> dataStream = SERIALISER.deserialise(bis)) {

            //now read everything in the file
            sink(dataStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
