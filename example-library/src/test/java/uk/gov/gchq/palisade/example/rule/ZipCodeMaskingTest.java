/*
 * Copyright 2019 Crown Copyright
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

package uk.gov.gchq.palisade.example.rule;

import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.example.common.Purpose;
import uk.gov.gchq.palisade.example.common.Role;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ZipCodeMaskingTest {
    private static final UserId TEST_USERID = new UserId().id("an id");
    private static final User TEST_USER_NOT_ESTATES_OR_HR = new User().roles("Not Estates").userId(TEST_USERID); // Role not in Estates or HR
    private static final User TEST_USER_ESTATES = new User().roles(Role.ESTATES.name()).userId(TEST_USERID); // Role is Estates
    private static final User TEST_USER_HR = new User().roles(Role.HR.name()).userId(TEST_USERID); // Role is HR
    private static final ZipCodeMaskingRule ZIP_CODE_MASKING_RULE = new ZipCodeMaskingRule();
    private static final Context STAFF_REPORT_CONTEXT = new Context().purpose(Purpose.STAFF_REPORT.name());

    private Employee TEST_EMPLOYEE;

    @Before
    public void setUp() {
        TEST_EMPLOYEE = Employee.generate(new Random(1));
    }

    @Test
    public void shouldNotRedactOrMaskForHR() {
        // Given - Employee, Role, Reason
        String originalZipCode = TEST_EMPLOYEE.getAddress().getZipCode();

        // When
        Employee actual = ZIP_CODE_MASKING_RULE.apply(TEST_EMPLOYEE, TEST_USER_HR, STAFF_REPORT_CONTEXT);

        // Then
        assertNotNull("Should not redact whole address for HR role", actual.getAddress());
        assertEquals("Should not mask zip code for HR role", originalZipCode, actual.getAddress().getZipCode());
        assertNotNull("Should not redact street name for HR role", actual.getAddress().getStreetName());
        assertNotNull("Should not redact street number for HR role", actual.getAddress().getStreetAddressNumber());
    }

    @Test
    public void shouldMaskZipCodeForEstates() {
        // Given - Employee, Role, Reason
        String originalZipCode = TEST_EMPLOYEE.getAddress().getZipCode();

        // When
        Employee actual = ZIP_CODE_MASKING_RULE.apply(TEST_EMPLOYEE, TEST_USER_ESTATES, STAFF_REPORT_CONTEXT);
        String actualZipCode = actual.getAddress().getZipCode();

        // Then
        assertNotNull("Should not redact whole address for Estates role", actual.getAddress());
        assertEquals("Should not mask first n-1 characters of zip code for Estates role", originalZipCode.substring(0, originalZipCode.length() - 1), actualZipCode.substring(0, originalZipCode.length() - 1));
        assertNotEquals("Should mask last 1 character of zip code for Estates role", originalZipCode.charAt(originalZipCode.length() - 1), actualZipCode.charAt(actualZipCode.length() - 1));
        assertNull("Should redact street number for Estates role", actual.getAddress().getStreetAddressNumber());
        assertNull("Should redact street name for Estates role", actual.getAddress().getStreetName());
    }

    @Test
    public void shouldRedactForNotEstatesAndNotHR() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = ZIP_CODE_MASKING_RULE.apply(TEST_EMPLOYEE, TEST_USER_NOT_ESTATES_OR_HR, STAFF_REPORT_CONTEXT);

        // Then
        assertNull("", actual.getAddress());
    }
}