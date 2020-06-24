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

package uk.gov.gchq.palisade.example.perf.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.util.function.IntSupplier;

public class ActionRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRunner.class);

    private final IntSupplier action;

    public ActionRunner(final IntSupplier action) {
        this.action = action;
    }

    @Override
    public void run(final String... args) {
        // call the action and exit with that return code
        try {
            int exitCode = action.getAsInt();
            System.exit(exitCode);

        } catch (RuntimeException e) {
            LOGGER.error("Executing \"{}\" failed:", action, e);
            System.exit(1);
        }
    }
}
