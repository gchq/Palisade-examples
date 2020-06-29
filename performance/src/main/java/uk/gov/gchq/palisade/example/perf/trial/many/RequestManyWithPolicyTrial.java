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

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Sets up a data request through Palisade, but doesn't read any data back.
 */
@Component
public class RequestManyWithPolicyTrial extends PalisadeTrial {
    static final String NAME = "request_many_with_policy";
    /**
     * Number of requests to make.
     */
    private static final int REQUESTS = 1;

    public RequestManyWithPolicyTrial(final Function<String, Stream<Employee>> client) {
        super(client);
        normal = Optional.of(RequestManyNoPolicyTrial.NAME);
    }

    public String name() {
        return NAME;
    }

    public String description() {
        return String.format("makes %d requests for many files without reading data", REQUESTS);
    }

    public void accept(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        for (int i = 0; i < REQUESTS; i++) {
            try (Stream<Employee> ignored = getDataStream(fileSet.manyDir)) {
                //do nothing
            }
        }
    }
}