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

package uk.gov.gchq.palisade.example.runner.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.runner.config.AkkaClientWrapper;

public class CommandLineExample implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineExample.class);

    private static final int USERID_ARGS = 1;
    private static final int FILENAME_ARGS = 2;
    private static final int PURPOSE_ARGS = 3;

    private final AkkaClientWrapper<Employee> client;

    public CommandLineExample(final AkkaClientWrapper<Employee> client) {
        this.client = client;
    }

    /**
     * Run a request through Palisade using command-line args for the userId, fileName and purpose
     *
     * @param args the command-line arguments
     * @throws Exception if not enough command-line arguments are given
     */
    @Override
    public void run(final String... args) throws Exception {
        try {
            String userId = args[USERID_ARGS];
            String fileName = args[FILENAME_ARGS];
            String purpose = args[PURPOSE_ARGS];

            this.client.run(userId, fileName, purpose);

            System.exit(0);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("Expected {} <userId> <fileName> <purpose>", args[0], ex);
            System.exit(1);
        }
    }
}
