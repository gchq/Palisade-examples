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
import uk.gov.gchq.syntheticdatagenerator.types.Address;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;
import uk.gov.gchq.syntheticdatagenerator.types.Manager;
import uk.gov.gchq.syntheticdatagenerator.types.WorkLocation;

import java.util.Objects;
import java.util.Set;

/**
 * A specific {@link Rule} implementation for the {@link Employee} address fields
 */
public class ZipCodeMaskingRule implements Rule<Employee> {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public ZipCodeMaskingRule() {
        // no-args constructor
    }

    private static Employee maskAddress(final Employee maskedRecord) {
        Address address = maskedRecord.getAddress();
        WorkLocation location = maskedRecord.getWorkLocation();
        Address workAddress = location.getAddress();
        String zipCode = address.getZipCode();
        String zipCodeRedacted = zipCode.substring(0, zipCode.length() - 1) + "*";
        String workZipCode = workAddress.getZipCode();
        String redactedWorkZipCode = workZipCode.substring(0, workZipCode.length() - 1) + "*";
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
        Objects.requireNonNull(user);
        Objects.requireNonNull(context);
        UserId userId = user.getUserId();
        Manager[] managers = record.getManager();
        Set<String> roles = user.getRoles();
        String purpose = context.getPurpose();

        if (roles.contains(Role.HR.name()) || (EmployeeUtils.isManager(managers, userId) && purpose.equals(Purpose.DUTY_OF_CARE.name()))) {
            return record;
        }

        if (roles.contains(Role.ESTATES.name())) {
            return maskAddress(record);
        } else {
            return redactAddress(record);
        }
    }
}




