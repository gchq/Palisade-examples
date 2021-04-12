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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.service.policy.common.Context;
import uk.gov.gchq.palisade.service.policy.common.User;
import uk.gov.gchq.palisade.service.policy.common.UserId;
import uk.gov.gchq.syntheticdatagenerator.types.Address;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ZipCodeMaskingTest {
    private static final UserId TEST_USERID = new UserId().id("an id");
    private static final User TEST_USER_NOT_ESTATES_OR_HR = new User().roles("Not Estates").userId(TEST_USERID); // Role not in Estates or HR
    private static final User TEST_USER_ESTATES = new User().roles(Role.ESTATES.name()).userId(TEST_USERID); // Role is Estates
    private static final User TEST_USER_HR = new User().roles(Role.HR.name()).userId(TEST_USERID); // Role is HR
    private static final ZipCodeMaskingRule ZIP_CODE_MASKING_RULE = new ZipCodeMaskingRule();
    private static final Context STAFF_REPORT_CONTEXT = new Context().purpose(Purpose.STAFF_REPORT.name());

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = Employee.generate(new Random(1));
    }

    @Test
    void testNotRedactOrMaskForHR() {
        // Given - Employee, Role, Reason
        var originalZipCode = testEmployee.getAddress().getZipCode();

        // When
        var actual = ZIP_CODE_MASKING_RULE.apply(testEmployee, TEST_USER_HR, STAFF_REPORT_CONTEXT);

        // Then
        assertThat(actual)
                .as("Should not redact whole address for HR role")
                .extracting(Employee::getAddress)
                .isNotNull();

        assertThat(actual.getAddress())
                .as("Should not redact street name or street address number for HR role")
                .extracting("streetName", "streetAddressNumber")
                .isNotNull();

        assertThat(actual)
                .as("Should not mask zip code for HR role")
                .extracting(Employee::getAddress)
                .extracting(Address::getZipCode)
                .isEqualTo(originalZipCode);
    }

    @Test
    void testMaskZipCodeForEstates() {
        // Given - Employee, Role, Reason
        var originalZipCode = testEmployee.getAddress().getZipCode();

        // When
        var actual = ZIP_CODE_MASKING_RULE.apply(testEmployee, TEST_USER_ESTATES, STAFF_REPORT_CONTEXT);
        var actualZipCode = actual.getAddress().getZipCode();

        // Then
        assertThat(actual)
                .as("Should not redact whole address for Estates role")
                .extracting(Employee::getAddress)
                .isNotNull();

        assertThat(actual.getAddress())
                .as("Should redact street name and street address number for Estates role")
                .extracting("streetName", "streetAddressNumber")
                .containsOnlyNulls();

        assertThat(originalZipCode.substring(0, originalZipCode.length() - 1))
                .as("Should not mask first n-1 characters of zip code for Estates role")
                .isEqualTo(actualZipCode.substring(0, originalZipCode.length() - 1));

        assertThat(originalZipCode.charAt(originalZipCode.length() - 1))
                .as("Should not mask first n-1 characters of zip code for Estates role")
                .isNotEqualTo(actualZipCode.charAt(actualZipCode.length() - 1));
    }

    @Test
    void testRedactForNotEstatesAndNotHR() {
        // Given - Employee, Role, Reason

        // When
        var actual = ZIP_CODE_MASKING_RULE.apply(testEmployee, TEST_USER_NOT_ESTATES_OR_HR, STAFF_REPORT_CONTEXT);

        // Then
        assertThat(actual)
                .as("Should redact zip code for not Estates and not HR")
                .extracting(Employee::getAddress)
                .isNull();
    }
}