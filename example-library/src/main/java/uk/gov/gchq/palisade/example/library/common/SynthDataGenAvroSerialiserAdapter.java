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

package uk.gov.gchq.palisade.example.library.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.gchq.syntheticdatagenerator.serialise.AvroSerialiser;
import uk.gov.gchq.syntheticdatagenerator.serialise.Serialiser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

public class SynthDataGenAvroSerialiserAdapter<T> implements Serialiser<T> {
    private final AvroSerialiser<T> delegate;

    public SynthDataGenAvroSerialiserAdapter(@JsonProperty("domainClass") final Class<T> domainClass) {
        this.delegate = new AvroSerialiser<>(domainClass);
    }

    @Override
    public Stream<T> deserialise(final InputStream input) throws IOException {
        return delegate.deserialise(input);
    }

    @Override
    public void serialise(final Stream<T> objects, final OutputStream output) throws IOException {
        delegate.serialise(objects, output);
    }

    public Class<T> getDomainClass() {
        return delegate.getDomainClass();
    }
}
