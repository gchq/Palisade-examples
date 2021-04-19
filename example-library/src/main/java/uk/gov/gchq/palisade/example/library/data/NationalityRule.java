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

package uk.gov.gchq.palisade.example.library.data;

import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.service.data.common.Context;
import uk.gov.gchq.palisade.service.data.common.RegisterJsonSubType;
import uk.gov.gchq.palisade.service.data.common.rule.Rule;
import uk.gov.gchq.palisade.service.data.common.user.User;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Set;

import static java.util.Objects.requireNonNull;

@RegisterJsonSubType(Rule.class)
public class NationalityRule implements Rule<Employee> {
    private static final long serialVersionUID = 1L;

    public NationalityRule() {
        // Empty Constructor
    }

    private static Employee redactRecord(final Employee redactedRecord) {
        redactedRecord.setNationality(null);
        return redactedRecord;
    }

    public Employee apply(final Employee record, final User user, final Context context) {
        if (null == record) {
            return null;
        }

        requireNonNull(user);
        requireNonNull(context);

        Set<String> roles = user.getRoles();
        String purpose = context.getPurpose();

        if (roles.contains(Role.HR.name()) && purpose.equals(Purpose.STAFF_REPORT.name())) {
            return record;
        }
        return redactRecord(record);
    }
}
