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
import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.user.User;
import uk.gov.gchq.palisade.user.UserId;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;
import uk.gov.gchq.syntheticdatagenerator.types.Manager;

import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A specific {@code Rule} implementation for the {@link Employee} duty of care role
 */
public class DutyOfCareRule implements Rule<Employee> {

    /**
     * Default constructor
     */
    public DutyOfCareRule() {
        // no-args constructor
    }

    private Employee redactRecord(final Employee redactedRecord) {
        redactedRecord.setContactNumbers(null);
        redactedRecord.setEmergencyContacts(null);
        redactedRecord.setSex(null);
        return redactedRecord;
    }

    /**
     * Applies the {@code Rule} to a record
     *
     * @param record the record being processed
     * @param user the {@code User} making the request
     * @param context the {@code Context}, including the purpose, of the request
     * @return the {@link Employee} record after the rule has been applied
     */
    public Employee apply(final Employee record, final User user, final Context context) {
        if (null == record) {
            return null;
        }

        requireNonNull(user);
        requireNonNull(context);
        Set<String> roles = user.getRoles();
        String purpose = context.getPurpose();
        UserId userId = user.getUserId();
        Manager[] managers = record.getManager();

        if (roles.contains(Role.HR.name()) && purpose.equals(Purpose.DUTY_OF_CARE.name())) {
            return record;
        } else if (EmployeeUtils.isManager(managers, userId) && purpose.equals(Purpose.DUTY_OF_CARE.name())) {
            return record;
        } else {
            return redactRecord(record);
        }
    }
}
