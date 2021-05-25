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

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.example.library.common.EmployeeUtils;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.user.User;
import uk.gov.gchq.palisade.user.UserId;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;
import uk.gov.gchq.syntheticdatagenerator.types.Manager;

import java.util.Objects;
import java.util.Set;

/**
 * A specific rule implementation for specific {@link Employee} fields
 */
public class RecordMaskingRule implements Rule<Employee> {

    /**
     * Default constructor
     */
    public RecordMaskingRule() {
        // no-args constructor
    }

    private Employee estatesRedactRecord(final Employee maskedRecord) {
        maskedRecord.setDateOfBirth(null);
        maskedRecord.setManager(null);
        maskedRecord.setHireDate(null);
        maskedRecord.setGrade(null);
        return maskedRecord;
    }

    /**
     * Applies the rule to a record
     *
     * @param record the record being processed
     * @param user the user making the request
     * @param context the context, including the purpose, of the request
     * @return the {@link Employee} record after the rule has been applied
     */
    public Employee apply(final Employee record, final User user, final Context context) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(context);
        UserId userId = user.getUserId();
        Manager[] managers = record.getManager();
        Set<String> roles = user.getRoles();

        if (roles.contains(Role.HR.name())) {
            return record;
        }
        if (EmployeeUtils.isManager(managers, userId)) {
            return record;
        }
        if (roles.contains(Role.ESTATES.name())) {
            return estatesRedactRecord(record);
        }
        return null;
    }
}
