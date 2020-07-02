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

package uk.gov.gchq.palisade.example.perf.trial.many;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.PerfTrial;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This test performs a native file read of many files in the 1st file set. This is done without going via Palisade, but
 * does try to deserialise the data.
 */
@Component
public class ReadManyNativeTrial extends PerfTrial {
    static final String NAME = "read_many_native";
    //create the serialiser
    private static final Serialiser<Employee> SERIALISER = new AvroSerialiser<>(Employee.class);

    public ReadManyNativeTrial() {
        normal = Optional.of(NAME);
    }

    public String name() {
        return NAME;
    }

    public String description() {
        return "performs a native read and deserialise of many files";
    }

    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        try (Stream<Path> manyFiles = Files.walk(fileSet.manyDir)) {
            manyFiles.filter(path -> path.toFile().isFile())
                    .forEach(file -> {
                        //read from file
                        try (InputStream bis = Files.newInputStream(file);
                             Stream<Employee> dataStream = SERIALISER.deserialise(bis)) {

                            //now read everything in the file
                            sink(dataStream);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
