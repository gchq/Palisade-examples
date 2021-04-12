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

import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.service.policy.common.Context;
import uk.gov.gchq.palisade.service.policy.common.User;
import uk.gov.gchq.palisade.service.policy.common.rule.Rule;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Objects;
import java.util.Set;

public class ZipCodeMaskingRule implements Rule<Employee> {
    private static final long serialVersionUID = 1L;

    public ZipCodeMaskingRule() {
        // Empty Constructor
    }

    private static Employee maskAddress(final Employee maskedRecord) {
        var address = maskedRecord.getAddress();
        var location = maskedRecord.getWorkLocation();
        var workAddress = location.getAddress();
        var zipCode = address.getZipCode();
        var zipCodeRedacted = zipCode.substring(0, zipCode.length() - 1) + "*";
        var workZipCode = workAddress.getZipCode();
        var redactedWorkZipCode = workZipCode.substring(0, workZipCode.length() - 1) + "*";
        address.setStreetAddressNumber(null);
        address.setStreetName(null);
        address.setZipCode(zipCodeRedacted);
        workAddress.setStreetAddressNumber(null);
        workAddress.setStreetName(null);
        workAddress.setZipCode(redactedWorkZipCode);
        location.setAddress(workAddress);
        return maskedRecord;
    }

    private static Employee redactAddress(final Employee maskedRecord) {
        maskedRecord.setAddress(null);
        return maskedRecord;
    }

    @SuppressWarnings("java:S1142") // Suppress number of returns in method
    public Employee apply(final Employee record, final User user, final Context context) {
        if (null == record) {
            return null;
        }
        Objects.requireNonNull(user);
        Objects.requireNonNull(context);


        Set<String> roles = user.getRoles();
        if (roles.contains(Role.HR.name())) {
            return record;
        }

        var userId = user.getUserId();
        var managers = record.getManager();
        var purpose = context.getPurpose();

        if (EmployeeUtils.isManager(managers, userId) && purpose.equals(Purpose.DUTY_OF_CARE.name())) {
            return record;
        }

        if (roles.contains(Role.ESTATES.name())) {
            return maskAddress(record);
        } else {
            return redactAddress(record);
        }
    }
}




