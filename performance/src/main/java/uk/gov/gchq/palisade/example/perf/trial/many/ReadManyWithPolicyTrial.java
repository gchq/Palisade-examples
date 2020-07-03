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

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.PalisadeTrial;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Reads many files a repeated number of times. This measures the entire interaction with Palisade.
 */
@Component
public class ReadManyWithPolicyTrial extends PalisadeTrial {
    protected static final String NAME = "read_many_with_policy";

    public ReadManyWithPolicyTrial(final Function<String, Stream<Employee>> client) {
        super(client);
        setNameForNormalisation(ReadManyNativeTrial.NAME);
    }

    public String name() {
        return NAME;
    }

    public String description() {
        return "reads many files with an example policy set";
    }

    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        //setup a request and read data
        try (Stream<Employee> data = getDataStream(fileSet.manyDir)) {
            sink(data);
        }
    }
}
