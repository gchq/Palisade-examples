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

import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.User;
import uk.gov.gchq.palisade.example.library.common.ExampleUser;
import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.example.library.common.TrainingCourse;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BankDetailsTest {

    private static final User HR_USER_WITHOUT_PAYROLL = new ExampleUser()
            .roles(Role.HR.name())
            .userId("UserId");
    private static final User HR_USER_WITH_PAYROLL = new ExampleUser()
            .trainingCompleted(TrainingCourse.PAYROLL_TRAINING_COURSE)
            .roles(Role.HR.name())
            .userId("UserId");
    private static final User USER_WITH_PAYROLL = new User()
            .roles(Role.HR.name())
            .userId("UserId");
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
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, HR_USER_WITH_PAYROLL, SALARY_CONTEXT);

        // Then
        assertNotNull("Bank details should not be redacted with HR role, payroll training course and salary purpose", actual.getBankDetails());
    }

    @Test
    public void shouldRedactIfNotSalaryPurpose() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, HR_USER_WITH_PAYROLL, NOT_SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted without salary purpose", actual.getBankDetails());
    }

    @Test
    public void shouldRedactIfNotPayrollTrained() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, HR_USER_WITHOUT_PAYROLL, SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted without payroll training course", actual.getBankDetails());
    }

    @Test
    public void shouldRedactIfNotHRRole() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = BANK_DETAILS_RULE.apply(testEmployee, USER_WITH_PAYROLL, NOT_SALARY_CONTEXT);

        // Then
        assertNull("Bank details should be redacted without HR role", actual.getBankDetails());
    }

}
