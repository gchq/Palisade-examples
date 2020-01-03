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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.common.Purpose;
import uk.gov.gchq.palisade.service.palisade.request.RegisterDataRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class RestExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExample.class);
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public static void main(final String... args) throws Exception {
        if (args.length < 1) {
            System.out.printf("Usage: %s file\n", RestExample.class.getTypeName());
            System.out.println("\nfile\tfile containing serialised Employee instances to read");
            System.exit(1);
        }

        String sourceFile = args[0];
        LOGGER.info("Going to request {} from Palisade", sourceFile);
        new RestExample().run(sourceFile);
    }

    public void run(final String sourceFile) {

        String requestString;
        CompletableFuture<HttpResponse<String>> aliceResults = new CompletableFuture<>();

        final User alice = ExampleUsers.getAlice();
        final User bob = ExampleUsers.getBob();
        final User eve = ExampleUsers.getEve();

        LOGGER.info("Creating data request...");
        RegisterDataRequest dataRequest = new RegisterDataRequest().userId(alice.getUserId()).resourceId("file://" + sourceFile);
        LOGGER.debug("Data Request: {}", dataRequest);

        //Set the purpose to SALARY for the RegisterDataRequest
        LOGGER.info("Alice [ " + alice.toString() + " } is reading the Employee file with a purpose of SALARY...");
        setSalaryContext(dataRequest);
        LOGGER.debug("Data Request: {}", dataRequest);

        try {
            requestString = this.mapper.readTree(this.mapper.writeValueAsString(dataRequest)).toString();
        } catch (Exception ex) {
            LOGGER.error("Error reading object: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }

        if (requestString != null) {
            LOGGER.info("Creating HttpRequest...");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(BodyPublishers.ofString(requestString))
                    .uri(URI.create("http://localhost:8084/registerDataRequest"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            LOGGER.info("Sending request to palisade service: {}, {}, {}", request.uri().getPath(), request.headers().toString(), requestString);
            try {
                aliceResults = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                LOGGER.info("Request sent... ");
            } catch (Exception ex) {
                LOGGER.error("Error sending request: {}", ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }

        LOGGER.info("Alice got back: {}", aliceResults.join());
        //aliceResults.map(Object::toString).forEach(LOGGER::info);

        /*LOGGER.info("");
        LOGGER.info("Alice [ " + alice.toString() + " } is reading the Employee file with a purpose of DUTY_OF_CARE...");
        final Stream<Employee> aliceResults2 = client.read(sourceFile, alice.getUserId().getId(), Purpose.DUTY_OF_CARE.name());
        LOGGER.info("Alice got back: ");
        aliceResults2.map(Object::toString).forEach(LOGGER::info);

        LOGGER.info("");
        LOGGER.info("Alice [ " + alice.toString() + " } is reading the Employee file with a purpose of STAFF_REPORT...");
        final Stream<Employee> aliceResults3 = client.read(sourceFile, alice.getUserId().getId(), Purpose.STAFF_REPORT.name());
        LOGGER.info("Alice got back: ");
        aliceResults3.map(Object::toString).forEach(LOGGER::info);

        LOGGER.info("");
        LOGGER.info("Bob [ " + bob.toString() + " } is reading the Employee file with a purpose of DUTY_OF_CARE...");
        final Stream<Employee> bobResults1 = client.read(sourceFile, bob.getUserId().getId(), Purpose.DUTY_OF_CARE.name());
        LOGGER.info("Bob got back: ");
        bobResults1.map(Object::toString).forEach(LOGGER::info);

        LOGGER.info("");
        LOGGER.info("Bob [ " + bob.toString() + " } is reading the Employee file with a purpose that is empty...");
        final Stream<Employee> bobResults2 = client.read(sourceFile, bob.getUserId().getId(), "");
        LOGGER.info("Bob got back: ");
        bobResults2.map(Object::toString).forEach(LOGGER::info);

        LOGGER.info("");
        LOGGER.info("Eve [ " + eve.toString() + " } is reading the Employee file with a purpose that is empty...");
        final Stream<Employee> eveResults1 = client.read(sourceFile, eve.getUserId().getId(), "");
        LOGGER.info("Eve got back: ");
        eveResults1.map(Object::toString).forEach(LOGGER::info);*/
    }

    private RegisterDataRequest setSalaryContext(final RegisterDataRequest request) {
        return request.context(new Context().purpose(Purpose.SALARY.name()));
    }
}
