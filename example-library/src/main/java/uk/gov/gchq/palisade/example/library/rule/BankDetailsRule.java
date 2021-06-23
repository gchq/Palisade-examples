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
import uk.gov.gchq.palisade.example.library.common.ExampleUser;
import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.example.library.common.TrainingCourse;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.user.User;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A specific {@link Rule} implementation for the {@link Employee} bank detail fields
 */
public class BankDetailsRule implements Rule<Employee> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public BankDetailsRule() {
        // no-args constructor
    }

    private Employee redactRecord(final Employee redactedRecord) {
        redactedRecord.setBankDetails(null);
        redactedRecord.setTaxCode(null);
        redactedRecord.setSalaryAmount(-1);
        redactedRecord.setSalaryBonus(-1);
        return redactedRecord;
    }

    /**
     * Applies the {@link Rule} to a record
     *
     * @param record the record being processed
     * @param user the {@link User} making the request
     * @param context the {@link Context}, including the purpose, of the request
     * @return the {@link Employee} record after the rule has been applied
     */
    public Employee apply(final Employee record, final User user, final Context context) {
        if (null == record) {
            return null;
        }
        requireNonNull(user);
        requireNonNull(context);

        if (user instanceof ExampleUser) {
            ExampleUser exampleUser = (ExampleUser) user;
            Set<TrainingCourse> trainingCompleted = exampleUser.getTrainingCompleted();
            Set<String> roles = exampleUser.getRoles();
            String purpose = context.getPurpose();

            if (trainingCompleted.contains(TrainingCourse.PAYROLL_TRAINING_COURSE) &&
                    purpose.equals(Purpose.SALARY.name()) &&
                    roles.contains(Role.HR.name())) {
                return record;
            }
        }
        return redactRecord(record);
    }
}
