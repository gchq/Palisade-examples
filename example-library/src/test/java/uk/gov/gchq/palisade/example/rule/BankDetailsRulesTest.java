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
import uk.gov.gchq.palisade.example.common.ExampleUser;
import uk.gov.gchq.palisade.example.common.Purpose;
import uk.gov.gchq.palisade.example.common.TrainingCourse;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;

import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BankDetailsRulesTest {

    private static final User EXAMPLE_USER_WITHOUT_PAYROLL = new ExampleUser().userId("UserId"); // Role not in Payroll
    private static final User EXAMPLE_USER_WITH_PAYROLL = new ExampleUser().trainingCompleted(TrainingCourse.PAYROLL_TRAINING_COURSE).userId("UserId"); // Role in Payroll
    private static final User STANDARD_USER = new User().userId("UserId"); // Role not in Payroll
    private static final BankDetailsRule BANK_DETAILS_RULE = new BankDetailsRule();
    private static final Context SALARY_CONTEXT = new Context().purpose(Purpose.SALARY.name());
    private static final Context NOT_SALARY_CONTEXT = new Context().purpose("Not Salary");

    private Employee testEmployee;

    @Before
    public void setUp() {
        testEmployee = Employee.generate(new Random(1));
    }

    @Test
    public void shouldNotRedactForPayrollAndSalary() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, EXAMPLE_USER_WITH_PAYROLL, SALARY_CONTEXT);

        // Then
        assertNotNull("Bank details should not be redacted with payroll training course and salary purpose", actual.getBankDetails());
    }

    @Test
    public void shouldRedactForPayrollAndNotSalary() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, EXAMPLE_USER_WITH_PAYROLL, NOT_SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted without salary purpose", actual.getBankDetails());
    }

    @Test
    public void shouldRedactForNotPayrollAndSalary() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, EXAMPLE_USER_WITHOUT_PAYROLL, SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted without payroll training course", actual.getBankDetails());
    }

    @Test
    public void shouldRedactForNotPayrollAndNotSalary() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, EXAMPLE_USER_WITHOUT_PAYROLL, NOT_SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted without payroll role and without salary purpose", actual.getBankDetails());
    }

    @Test
    public void shouldRedactForStandardUser() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, STANDARD_USER, SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted if the user is not an ExampleUser (no such trainingCourses member var)", actual.getBankDetails());
    }

}
