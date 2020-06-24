/*
 * Copyright 2018 Crown Copyright
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

package uk.gov.gchq.palisade.example.perf.trial;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Test that reads the large data file from Palisade with an example policy and times entire Palisade interaction.
 */
@Component
public class ReadLargeWithPolicyTrial extends PalisadeTrial {
    public ReadLargeWithPolicyTrial(final Function<String, Stream<Employee>> client) {
        super(client);
    }

    public String name() {
        return "read_large_with_policy";
    }

    public String description() {
        return "reads the large data file with an example policy set";
    }

    public void accept(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        requireNonNull(fileSet, "fileSet");
        requireNonNull(noPolicySet, "noPolicySet");

        //setup a request and read data
        try (Stream<Employee> data = getDataStream(fileSet.largeFile)) {
            sink(data);
        }
    }
}
