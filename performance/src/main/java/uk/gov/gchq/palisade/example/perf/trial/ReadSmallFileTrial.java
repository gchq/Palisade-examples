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
 * Reads the small file a repeated number of times. This measures the entire interaction with Palisade.
 */
@Component
public class ReadSmallFileTrial extends PalisadeTrial {
    /**
     * Number of requests to make.
     */
    private static final int REQUESTS = 1;

    public ReadSmallFileTrial(final Function<String, Stream<Employee>> client, final int requests) {
        super(client);
        if (requests < 1) {
            throw new IllegalArgumentException("requests less than 1");
        }
    }

    public String name() {
        return "read_small_with_policy";
    }

    public String description() {
        return String.format("reads the small file %d times", REQUESTS);
    }

    public void accept(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        requireNonNull(fileSet, "fileSet");
        requireNonNull(noPolicySet, "noPolicySet");

        //make multiple requests
        for (int i = 0; i < REQUESTS; i++) {
            //setup a request and read data
            try (Stream<Employee> data = getDataStream(fileSet.smallFile)) {
                sink(data);
            }
        }
    }
}
