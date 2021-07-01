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

import uk.gov.gchq.palisade.user.UserId;
import uk.gov.gchq.syntheticdatagenerator.types.Manager;

/**
 * Contains utility methods for an {@link uk.gov.gchq.syntheticdatagenerator.types.Employee}
 */
public final class EmployeeUtils {

    /**
     * Default constructor
     */
    private EmployeeUtils() {
        //no-args constructor
    }

    /**
     * Checks if the user is in the management chain of an {@link uk.gov.gchq.syntheticdatagenerator.types.Employee}
     *
     * @param managers the manager array for an Employee
     * @param userId the ID of the user making the request
     * @return a boolean value
     */
    public static boolean isManager(final Manager[] managers, final UserId userId) {
        if (managers == null) {
            return false;
        }

        for (Manager manager : managers) {
            if (manager.getUid().equals(userId.getId())) {
                return true;
            }
        }

        for (Manager manager : managers) {
            if (isManager(manager.getManager(), userId)) {
                return true;
            }
        }

        return false;
    }
}
