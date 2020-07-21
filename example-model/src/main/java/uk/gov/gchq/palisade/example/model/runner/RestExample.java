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

package uk.gov.gchq.palisade.example.model.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.model.client.ExampleSimpleClient;

import java.io.IOException;
import java.util.stream.Stream;

public class RestExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExample.class);

    ExampleSimpleClient client;

    public RestExample(final ExampleSimpleClient exampleSimpleClient) {
        this.client = exampleSimpleClient;
    }

    private void makeRequest(final String userId, final String resourceId, final String purpose) throws IOException {
        LOGGER.info("");
        LOGGER.info("{} is reading {} with a purpose of {}...", userId, resourceId, purpose);
        final Stream<Employee> results = client.read(resourceId, userId, purpose);
        LOGGER.info("{} got back: ", userId);
        results.map(Object::toString).forEach(LOGGER::info);
    }

    public void run(final String employeeFile) throws IOException {
        final String alice = "Alice";
        final String bob = "Bob";
        final String eve = "Eve";

        final String salary = Purpose.SALARY.name();
        final String dutyOfCare = Purpose.DUTY_OF_CARE.name();
        final String staffReport = Purpose.STAFF_REPORT.name();

        //Alice is reading the employee file with a purpose of SALARY
        makeRequest(alice, employeeFile, salary);

        //Alice is reading the employee file with a purpose of DUTY OF CARE
        makeRequest(alice, employeeFile, dutyOfCare);

        //Alice is reading the employee file with a purpose of STAFF REPORT
        makeRequest(alice, employeeFile, staffReport);

        //Bob is reading the employee file with a purpose of DUTY OF CARE
        makeRequest(bob, employeeFile, dutyOfCare);

        //Bob is reading the employee file with a purpose that is empty
        makeRequest(bob, employeeFile, "");

        //Eve is reading the employee file with a purpose that is empty
        makeRequest(eve, employeeFile, "");
    }
}
