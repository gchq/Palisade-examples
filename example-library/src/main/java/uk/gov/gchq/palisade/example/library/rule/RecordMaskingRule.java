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

package uk.gov.gchq.palisade.example.library.rule;

import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.service.policy.common.Context;
import uk.gov.gchq.palisade.service.policy.common.rule.Rule;
import uk.gov.gchq.palisade.service.policy.common.user.User;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Objects;

public class RecordMaskingRule implements Rule<Employee> {
    private static final long serialVersionUID = 1L;

    public RecordMaskingRule() {
        // Empty Constructor
    }

    private static Employee estatesRedactRecord(final Employee maskedRecord) {
        maskedRecord.setDateOfBirth(null);
        maskedRecord.setManager(null);
        maskedRecord.setHireDate(null);
        maskedRecord.setGrade(null);
        return maskedRecord;
    }

    public Employee apply(final Employee record, final User user, final Context context) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(context);

        var roles = user.getRoles();
        if (roles.contains(Role.HR.name())) {
            return record;
        }

        var userId = user.getUserId();
        var managers = record.getManager();

        if (EmployeeUtils.isManager(managers, userId)) {
            return record;
        }

        if (roles.contains(Role.ESTATES.name())) {
            return estatesRedactRecord(record);
        }
        return null;
    }
}
