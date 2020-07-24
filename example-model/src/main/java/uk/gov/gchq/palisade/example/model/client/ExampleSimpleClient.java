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
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

public class ExampleSimpleClient extends SimpleClient<Employee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSimpleClient.class);
    private static final String FORMAT = "file:%s";

    public ExampleSimpleClient(final PalisadeClient palisadeClient, final DataClientFactory dataClient) {
        super(new AvroSerialiser<>(Employee.class), palisadeClient, dataClient);
    }

    public void run(final String filename, final String userId, final String purpose) throws IOException {
        LOGGER.info("{} is reading the Employee file {} with a purpose of {}", userId, filename, purpose);
        final Stream<Stream<Employee>> results = read(filename, userId, purpose);
        LOGGER.info("{} got back:", userId);
        // We are going to read all resources and all records
        // So we won't be leaving any dangling connections
        // Therefore it is safe to flatMap and open up a connection for every resource simultaneously
        results.flatMap(Function.identity())
                .map(Employee::toString)
                .forEach(LOGGER::info);
    }

    /**
     * Given a name for either a directory of many files, or a single file, containing Employee AVRO data,
     * format this fileName to a URI resourceId, then read from the SimpleClient.
     *
     * @param fileName   the absolute or (if it exists locally) relative filename for the resource
     * @param userId     the user id
     * @param purpose    the purpose
     * @return a stream of Employee objects from palisade
     * @throws IOException if an exception occurred deserialising data
     */
    public Stream<Stream<Employee>> read(final String fileName, final String userId, final String purpose) throws IOException {
        final String resourceName;
        if (!Path.of(fileName).isAbsolute()) {
            // If a relative path is requested, this implies it is available locally
            // We can be sure that this path is appropriately a directory "/some/dir " or a file "/some/file"
            resourceName = new File(fileName).getCanonicalPath();
        } else {
            // Otherwise, it could be either, so carefully preserve the exact request
            // If we tried to resolve a directory that doesn't exist, Java will treat it as a file and drop the trailing "/"
            resourceName = fileName;
        }
        // Again, we cannot trust any toURI methods to not drop a trailing "/"
        final String resourceId = String.format(FORMAT, resourceName);

        LOGGER.debug("Formatted fileName {} to resourceName {} to resourceId {}", fileName, resourceName, resourceId);
        return super.read(resourceId, userId, purpose);
    }
}
