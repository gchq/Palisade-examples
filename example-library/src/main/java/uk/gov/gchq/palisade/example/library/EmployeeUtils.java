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

package uk.gov.gchq.palisade.example.library;

import uk.gov.gchq.syntheticdatagenerator.types.Manager;

public final class EmployeeUtils {

    private EmployeeUtils() {
    }

    public static boolean isManager(final Manager[] managers, final uk.gov.gchq.palisade.service.policy.common.user.UserId userId) {
        return isManager(managers, userId.getId());
    }

    public static boolean isManager(final Manager[] managers, final uk.gov.gchq.palisade.service.data.common.user.UserId userId) {
        return isManager(managers, userId.getId());
    }

    public static boolean isManager(final Manager[] managers, final String userId) {
        if (managers == null) {
            return false;
        }

        for (Manager manager : managers) {
            if (manager.getUid().equals(userId)) {
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
