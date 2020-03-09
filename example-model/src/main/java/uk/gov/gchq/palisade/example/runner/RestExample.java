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

package uk.gov.gchq.palisade.example.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;

import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.client.ExampleSimpleClient;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.common.Purpose;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

public class RestExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExample.class);

    ExampleSimpleClient client;

    public RestExample(final ExampleSimpleClient exampleSimpleClient) {
        this.client = exampleSimpleClient;
    }

    public void run(final ApplicationArguments appArgs) {
        run(appArgs.getSourceArgs());
    }

    public void run(final String[] args) {
        if (args.length < 1) {
            LOGGER.info("Usage: {} file\n", RestExample.class.getTypeName());
            LOGGER.info("file \t file containing serialised Employee instances to read");
            System.exit(1);
        }

        String sourceFile = args[0];
        LOGGER.info("");
        LOGGER.info("Going to request {} from Palisade", sourceFile);
        try {
            this.run(sourceFile);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void run(final String sourceFile) throws IOException, URISyntaxException {
        final User alice = ExampleUsers.getAlice();
        final User bob = ExampleUsers.getBob();
        final User eve = ExampleUsers.getEve();

        //Alice is reading the employee file with a purpose of SALARY
        LOGGER.info("");
        LOGGER.info("Alice [ " + alice.toString() + " } is reading the Employee file with a purpose of SALARY...");
        final Stream<Employee> aliceResults = client.read(sourceFile, alice.getUserId().getId(), Purpose.SALARY.name());
        LOGGER.info("Alice got back: ");
        aliceResults.map(Object::toString).forEach(LOGGER::info);

        //Alice is reading the employee file with a purpose of DUTY OF CARE
        LOGGER.info("");
        LOGGER.info("Alice [ " + alice.toString() + " } is reading the Employee file with a purpose of DUTY_OF_CARE...");
        final Stream<Employee> aliceResults2 = client.read(sourceFile, alice.getUserId().getId(), Purpose.DUTY_OF_CARE.name());
        LOGGER.info("Alice got back: ");
        aliceResults2.map(Object::toString).forEach(LOGGER::info);

        //Alice is reading the employee file with a purpose of STAFF REPORT
        LOGGER.info("");
        LOGGER.info("Alice [ " + alice.toString() + " } is reading the Employee file with a purpose of STAFF_REPORT...");
        final Stream<Employee> aliceResults3 = client.read(sourceFile, alice.getUserId().getId(), Purpose.STAFF_REPORT.name());
        LOGGER.info("Alice got back: ");
        aliceResults3.map(Object::toString).forEach(LOGGER::info);

        //Bob is reading the employee file with a purpose of DUTY OF CARE
        LOGGER.info("");
        LOGGER.info("Bob [ " + bob.toString() + " } is reading the Employee file with a purpose of DUTY_OF_CARE...");
        final Stream<Employee> bobResults1 = client.read(sourceFile, bob.getUserId().getId(), Purpose.DUTY_OF_CARE.name());
        LOGGER.info("Bob got back: ");
        bobResults1.map(Object::toString).forEach(LOGGER::info);

        //Bob is reading the employee file with a purpose that is empty
        LOGGER.info("");
        LOGGER.info("Bob [ " + bob.toString() + " } is reading the Employee file with a purpose that is empty...");
        final Stream<Employee> bobResults2 = client.read(sourceFile, bob.getUserId().getId(), "");
        LOGGER.info("Bob got back: ");
        bobResults2.map(Object::toString).forEach(LOGGER::info);

        //Eve is reading the employee file with a purpose that is empty
        LOGGER.info("");
        LOGGER.info("Eve [ " + eve.toString() + " } is reading the Employee file with a purpose that is empty...");
        final Stream<Employee> eveResults1 = client.read(sourceFile, eve.getUserId().getId(), "");
        LOGGER.info("Eve got back: ");
        eveResults1.map(Object::toString).forEach(LOGGER::info);
    }
}
