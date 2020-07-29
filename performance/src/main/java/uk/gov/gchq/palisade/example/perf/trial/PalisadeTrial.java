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

package uk.gov.gchq.palisade.example.perf.trial;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public abstract class PalisadeTrial extends PerfTrial {
    private Function<String, Stream<Stream<Employee>>> client;

    /**
     * Default constructor
     *
     * @param client a pre-configured client (eg. userId Alice and purpose SALARY for Employee Avro files)
     */
    public PalisadeTrial(final Function<String, Stream<Stream<Employee>>> client) {
        this.client = client;
    }

    /**
     * Makes a request for the named resource to the given Palisade entry point. This serves to set up the system ready
     * to read data from Palisade.
     *
     * @param resource the resource to request from Palisade
     * @return data stream
     * @throws IllegalArgumentException if {@code resourceName} is empty
     */
    protected Stream<Stream<Employee>> getDataStream(final String resource) {
        requireNonNull(resource, "resourceName");
        return client.apply(resource);
    }
}
