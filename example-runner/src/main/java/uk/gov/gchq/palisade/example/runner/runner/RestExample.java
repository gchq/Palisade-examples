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

package uk.gov.gchq.palisade.example.runner.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.runner.config.AkkaClientWrapper;
import uk.gov.gchq.palisade.example.runner.config.RestConfiguration;

import java.io.IOException;

public class RestExample implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExample.class);

    private final RestConfiguration configuration;
    private final AkkaClientWrapper<Employee> client;

    public RestExample(final RestConfiguration configuration, final AkkaClientWrapper<Employee> client) {
        this.configuration = configuration;
        this.client = client;
    }

    private void makeRequest(final String userId, final String fileName, final String purpose) {
        LOGGER.info("");
        LOGGER.info("'{}' is reading '{}' with a purpose of '{}'...", userId, fileName, purpose);
        LOGGER.info("'{}' got back: ", userId);
        client.run(userId, fileName, purpose);
    }

    /**
     * The runner method to run some example requests through Palisade
     *
     * @param args command-line arguments
     * @throws IOException for any file system error
     */
    public void run(final String... args) throws IOException {
        final String alice = "Alice";
        final String bob = "Bob";
        final String eve = "Eve";

        final String salary = Purpose.SALARY.name();
        final String dutyOfCare = Purpose.DUTY_OF_CARE.name();
        final String staffReport = Purpose.STAFF_REPORT.name();

        //Alice is reading the employee file with a purpose of SALARY
        makeRequest(alice, configuration.getFilename(), salary);

        //Alice is reading the employee file with a purpose of DUTY OF CARE
        makeRequest(alice, configuration.getFilename(), dutyOfCare);

        //Alice is reading the employee file with a purpose of STAFF REPORT
        makeRequest(alice, configuration.getFilename(), staffReport);

        //Bob is reading the employee file with a purpose of DUTY OF CARE
        makeRequest(bob, configuration.getFilename(), dutyOfCare);

        //Bob is reading the employee file with a purpose that is empty
        makeRequest(bob, configuration.getFilename(), "");

        //Eve is reading the employee file with a purpose that is empty
        makeRequest(eve, configuration.getFilename(), "");

        System.exit(0);
    }
}
