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

package uk.gov.gchq.palisade.example.model.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.clients.simpleclient.client.SimpleClient;
import uk.gov.gchq.palisade.clients.simpleclient.web.DataClientFactory;
import uk.gov.gchq.palisade.clients.simpleclient.web.PalisadeClient;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class ExampleSimpleClient extends SimpleClient<Employee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSimpleClient.class);

    public ExampleSimpleClient(final PalisadeClient palisadeClient, final DataClientFactory dataClient) {
        super(new AvroSerialiser<>(Employee.class), palisadeClient, dataClient);
    }

    public void run(final String filename, final String userId, final String purpose) throws IOException {
        LOGGER.info("{} is reading the Employee file {} with a purpose of {}", userId, filename, purpose);
        final Stream<Employee> results = read(filename, userId, purpose);
        LOGGER.info("{} got back:", userId);
        results.map(Object::toString).forEach(LOGGER::info);
    }

    public Stream<Employee> read(final String resourceId, final String userId, final String purpose) throws IOException {
        String absoluteFile = new File(resourceId).toURI().toString();
        // Check that the last char of the resourceId is '/' or '\' for Windows Users, and then readd it if it missing
        char lastResourceChar = resourceId.charAt(resourceId.length() - 1);
        if (lastResourceChar != absoluteFile.charAt(absoluteFile.length() - 1)) {
            absoluteFile += lastResourceChar;
        }
        return super.read(absoluteFile, userId, purpose);
    }
}
