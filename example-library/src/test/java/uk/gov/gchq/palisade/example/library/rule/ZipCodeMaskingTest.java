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

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.user.User;
import uk.gov.gchq.palisade.user.UserId;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ZipCodeMaskingTest {
    private static final UserId TEST_USERID = new UserId().id("an id");
    private static final User TEST_USER_NOT_ESTATES_OR_HR = new User().roles(Collections.singleton("Not Estates")).userId(TEST_USERID); // Role not in Estates or HR
    private static final User TEST_USER_ESTATES = new User().roles(Collections.singleton(Role.ESTATES.name())).userId(TEST_USERID); // Role is Estates
    private static final User TEST_USER_HR = new User().roles(Collections.singleton(Role.HR.name())).userId(TEST_USERID); // Role is HR
    private static final ZipCodeMaskingRule ZIP_CODE_MASKING_RULE = new ZipCodeMaskingRule();
    private static final Context STAFF_REPORT_CONTEXT = new Context().purpose(Purpose.STAFF_REPORT.name());

    private Employee testEmployee;

    @BeforeEach
    public void setUp() {
        testEmployee = Employee.generate(new Random(1));
    }

    @Test
    void shouldNotRedactOrMaskForHR() {
        // Given - Employee, Role, Reason
        String originalZipCode = testEmployee.getAddress().getZipCode();

        // When
        Employee actual = ZIP_CODE_MASKING_RULE.apply(testEmployee, TEST_USER_HR, STAFF_REPORT_CONTEXT);

        // Then
        assertAll(
                () -> assertThat(actual.getAddress())
                        .as("Should not redact whole address for HR role")
                        .isNotNull(),
                () -> assertThat(originalZipCode)
                        .as("Should not mask zip code for HR role")
                        .isEqualTo(actual.getAddress().getZipCode()),
                () -> assertThat(actual.getAddress().getStreetName())
                        .as("Should not redact street name for HR role")
                        .isNotNull(),
                () -> assertThat(actual.getAddress().getStreetAddressNumber())
                        .as("Should not redact street number for HR role")
                        .isNotNull()
        );
    }

    @Test
    void shouldMaskZipCodeForEstates() {
        // Given - Employee, Role, Reason
        String originalZipCode = testEmployee.getAddress().getZipCode();

        // When
        Employee actual = ZIP_CODE_MASKING_RULE.apply(testEmployee, TEST_USER_ESTATES, STAFF_REPORT_CONTEXT);
        String actualZipCode = actual.getAddress().getZipCode();

        // Then
        assertAll(
                () -> assertThat(actual.getAddress())
                        .as("Should not redact whole address for Estates role")
                        .isNotNull(),
                () -> assertThat(actualZipCode.substring(0, originalZipCode.length() - 1))
                        .as("Should not mask first n-1 characters of zip code for Estates role")
                        .isEqualTo(originalZipCode.substring(0, originalZipCode.length() - 1)),
                () -> assertThat(actualZipCode.charAt(actualZipCode.length() - 1))
                        .as("Should mask last 1 character of zip code for Estates role")
                        .isNotEqualTo(originalZipCode.charAt(originalZipCode.length() - 1)),
                () -> assertThat(actual.getAddress().getStreetAddressNumber())
                        .as("Should redact street number for Estates role")
                        .isNull(),
                () -> assertThat(actual.getAddress().getStreetName())
                        .as("Should redact street name for Estates role")
                        .isNull()
        );
    }

    @Test
    void shouldRedactForNotEstatesAndNotHR() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = ZIP_CODE_MASKING_RULE.apply(testEmployee, TEST_USER_NOT_ESTATES_OR_HR, STAFF_REPORT_CONTEXT);

        // Then
        assertThat(actual.getAddress())
                .as("Should redact zip code for not Estates and not HR")
                .isNull();
    }
}