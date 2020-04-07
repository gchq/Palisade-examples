/*
 * Copyright 2019 Crown Copyright
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

package uk.gov.gchq.palisade.example.client;

import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.example.web.DataClient;
import uk.gov.gchq.palisade.example.web.PalisadeClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

public class ExampleSimpleClient extends SimpleClient<Employee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSimpleClient.class);
    private static final String RESOURCE_TYPE = "employee";

    public ExampleSimpleClient(final PalisadeClient palisadeClient, final DataClient dataClient, final EurekaClient eurekaClient) {
        super(new AvroSerialiser<>(Employee.class), palisadeClient, dataClient, eurekaClient);
    }

    public void run(final String userId, final String filename, final String purpose) throws IOException, URISyntaxException {
        LOGGER.info("{} is reading the Employee file {} with a purpose of {}", userId, filename, purpose);
        final Stream<Employee> results = read(filename, userId, purpose);
        LOGGER.info("{} got back:", userId);
        results.map(Object::toString).forEach(LOGGER::info);
    }

    public Stream<Employee> read(final String filename, final String userId, final String purpose) throws IOException, URISyntaxException {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(filename);
        String absoluteFile = absoluteFileURI.toString();
        return super.read(absoluteFile, RESOURCE_TYPE, userId, purpose);
    }
}
