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

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Sets up a data request through Palisade, but doesn't read any data back.
 */
@Component
public class RequestOnlyTrial extends PalisadeTrial {
    /**
     * Number of requests to make.
     */
    private static final int REQUESTS = 1;

    public RequestOnlyTrial(final Function<String, Stream<Employee>> client) {
        super(client);
    }

    public String name() {
        return "request_small_with_policy";
    }

    public String description() {
        return String.format("makes %d requests without reading data", REQUESTS);
    }

    public void accept(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        requireNonNull(fileSet, "fileSet");
        requireNonNull(noPolicySet, "noPolicySet");
        for (int i = 0; i < REQUESTS; i++) {
            try (Stream<Employee> data = getDataStream(fileSet.smallFile)) {
                //do nothing
            }
        }
    }
}
