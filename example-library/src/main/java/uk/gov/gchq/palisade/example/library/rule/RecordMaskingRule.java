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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Manager;
import uk.gov.gchq.palisade.example.library.common.EmployeeUtils;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.rule.Rule;

import java.util.Objects;
import java.util.Set;

public class RecordMaskingRule implements Rule<Employee> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordMaskingRule.class);

    public RecordMaskingRule() {
    }

    private Employee estatesRedactRecord(final Employee maskedRecord) {
        maskedRecord.setDateOfBirth(null);
        maskedRecord.setManager(null);
        maskedRecord.setHireDate(null);
        maskedRecord.setGrade(null);
        return maskedRecord;
    }

    public Employee apply(final Employee record, final User user, final Context context) {
        LOGGER.warn("\n\t Employee: {} \n\t User: {} \n\t Context: {}", record, user, context);

        Objects.requireNonNull(user);
        Objects.requireNonNull(context);
        UserId userId = user.getUserId();
        Manager[] managers = record.getManager();
        Set<String> roles = user.getRoles();

        if (roles.contains(Role.HR.name())) {
            LOGGER.warn("Is HR");
            return record;
        }
        if (EmployeeUtils.isManager(managers, userId)) {
            LOGGER.warn("Is manager");
            return record;
        }
        if (roles.contains(Role.ESTATES.name())) {
            LOGGER.warn("Is estates");
            return estatesRedactRecord(record);
        }
        LOGGER.warn("Redact ENTIRE RECORD as no prereqs apply");
        return null;
    }
}
