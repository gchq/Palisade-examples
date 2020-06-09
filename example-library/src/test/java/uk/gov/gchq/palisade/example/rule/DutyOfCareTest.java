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
 * See the License if the specific language governing permissions and
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DutyOfCareTest {

    private static final User FIRST_MANAGER = new User().userId(new UserId().id("1962720332")).roles("Not HR"); // Start of chain and not in HR
    private static final User MIDDLE_MANAGER = new User().userId(new UserId().id("1816031731")).roles("Not HR"); // Middle of chain and not HR
    private static final User END_MANAGER = new User().userId(new UserId().id("1501105288")).roles("Not HR"); // End of chain and not HR
    private static final User HR_USER = new User().userId(new UserId().id("1")).roles(Role.HR.name()); // Not in chain and HR
    private static final User NON_HR_USER = new User().userId(new UserId().id("1")).roles("Not HR"); // Not in chain and not HR
    private static final DutyOfCareRule DUTY_OF_CARE_RULE = new DutyOfCareRule();
    private static final Context DUTY_OF_CARE_CONTEXT = new Context().purpose(Purpose.DUTY_OF_CARE.name());
    private static final Context NOT_DUTY_OF_CARE_CONTEXT = new Context().purpose("Not Duty of Care");

    private Employee TEST_EMPLOYEE;

    @Before
    public void setUp() {
        TEST_EMPLOYEE = Employee.generate(new Random(2));
        TEST_EMPLOYEE.getManager()[0].setUid(FIRST_MANAGER.getUserId());
        TEST_EMPLOYEE.getManager()[0].getManager()[0].setUid(MIDDLE_MANAGER.getUserId());
        TEST_EMPLOYEE.getManager()[0].getManager()[0].getManager()[0].setUid(END_MANAGER.getUserId());
    }

    @Test
    public void shouldNotRedactForStartOfManagerInChain() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, FIRST_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertNotNull("Should not redact contact numbers if first manager in chain", actual.getContactNumbers());
        assertNotNull("Should not redact emergency contacts if first manager in chain", actual.getEmergencyContacts());
        assertNotNull("Should not redact sex if first manager in chain", actual.getSex());
    }

    @Test
    public void shouldNotRedactForMiddleManagerInChain() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, MIDDLE_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertNotNull("Should not redact contact numbers if middle manager in chain", actual.getContactNumbers());
        assertNotNull("Should not redact emergency contacts if middle manager in chain", actual.getEmergencyContacts());
        assertNotNull("Should not redact sex if middle manager in chain", actual.getSex());
    }

    @Test
    public void shouldNotRedactForEndManagerInChain() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, END_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertNotNull("Should not redact contact numbers if end manager in chain", actual.getContactNumbers());
        assertNotNull("Should not redact emergency contacts if end manager in chain", actual.getEmergencyContacts());
        assertNotNull("Should not redact sex if end manager in chain", actual.getSex());
    }

    @Test
    public void shouldNotRedactForHRAndDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, HR_USER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertNotNull("Should not redact contact numbers if hr role with duty of care", actual.getContactNumbers());
        assertNotNull("Should not redact emergency contacts if hr role with duty of care", actual.getEmergencyContacts());
        assertNotNull("Should not redact sex if hr role with duty of care", actual.getSex());
    }

    @Test
    public void shouldRedactForNotManagerAndNotHR() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, NON_HR_USER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertNull("Should redact contact numbers if not manager and not hr", actual.getContactNumbers());
        assertNull("Should redact emergency contacts if not manager and not hr", actual.getEmergencyContacts());
        assertNull("Should redact sex if not manager and not hr", actual.getSex());
    }

    @Test
    public void shouldRedactForEndManagerInChainNotDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, END_MANAGER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertNull("Should redact contact numbers if end manager in chain without duty of care", actual.getContactNumbers());
        assertNull("Should redact emergency contacts if end manager in chain without duty of care", actual.getEmergencyContacts());
        assertNull("Should redact sex if end manager in chain without duty of care", actual.getSex());
    }

    @Test
    public void shouldRedactForHRAndNotDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, HR_USER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertNull("Should redact contact numbers if hr role without duty of care", actual.getContactNumbers());
        assertNull("Should redact emergency contacts if hr role without duty of care", actual.getEmergencyContacts());
        assertNull("Should redact sex if hr role without duty of care", actual.getSex());
    }

    @Test
    public void shouldRedactForNotManagerAndNotHRAndNotDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(TEST_EMPLOYEE, NON_HR_USER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertNull("Should redact contact numbers if not manager, not hr role and not duty of care purpose", actual.getContactNumbers());
        assertNull("Should redact emergency contacts if not manager, not hr role and not duty of care purpose", actual.getEmergencyContacts());
        assertNull("Should redact sex if not manager, not hr role and not duty of care purpose", actual.getSex());
    }
}
