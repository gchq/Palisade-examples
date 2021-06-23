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

class DutyOfCareTest {

    private static final User FIRST_MANAGER = new User().userId(new UserId().id("1962720332")).roles(Collections.singleton("Not HR")); // Start of chain and not in HR
    private static final User MIDDLE_MANAGER = new User().userId(new UserId().id("1816031731")).roles(Collections.singleton("Not HR")); // Middle of chain and not HR
    private static final User END_MANAGER = new User().userId(new UserId().id("1501105288")).roles(Collections.singleton("Not HR")); // End of chain and not HR
    private static final User HR_USER = new User().userId(new UserId().id("1")).roles(Collections.singleton(Role.HR.name())); // Not in chain and HR
    private static final User NON_HR_USER = new User().userId(new UserId().id("1")).roles(Collections.singleton("Not HR")); // Not in chain and not HR
    private static final DutyOfCareRule DUTY_OF_CARE_RULE = new DutyOfCareRule();
    private static final Context DUTY_OF_CARE_CONTEXT = new Context().purpose(Purpose.DUTY_OF_CARE.name());
    private static final Context NOT_DUTY_OF_CARE_CONTEXT = new Context().purpose("Not Duty of Care");

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = Employee.generate(new Random(2));
        testEmployee.getManager()[0].setUid(FIRST_MANAGER.getUserId().getId());
        testEmployee.getManager()[0].getManager()[0].setUid(MIDDLE_MANAGER.getUserId().getId());
        testEmployee.getManager()[0].getManager()[0].getManager()[0].setUid(END_MANAGER.getUserId().getId());
    }

    @Test
    void shouldNotRedactForStartOfManagerInChain() {
        // Given - Employee, Role, Reason

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, FIRST_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should not redact contact numbers if first manager in chain")
                .isNotNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should not redact emergency contacts if first manager in chain")
                .isNotNull();
        assertThat(actual.getSex())
                .as("Should not redact sex if first manager in chain")
                .isNotNull();
    }

    @Test
    void shouldNotRedactForMiddleManagerInChain() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, MIDDLE_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should not redact contact numbers if middle manager in chain")
                .isNotNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should not redact emergency contacts if middle manager in chain")
                .isNotNull();
        assertThat(actual.getSex())
                .as("Should not redact sex if middle manager in chain")
                .isNotNull();
    }

    @Test
    void shouldNotRedactForEndManagerInChain() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, END_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should not redact contact numbers if end manager in chain")
                .isNotNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should not redact emergency contacts if end manager in chain")
                .isNotNull();
        assertThat(actual.getSex())
                .as("Should not redact sex if end manager in chain")
                .isNotNull();
    }

    @Test
    void shouldNotRedactForHRAndDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, HR_USER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should not redact contact numbers if hr role with duty of care")
                .isNotNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should not redact emergency contacts if hr role with duty of care")
                .isNotNull();
        assertThat(actual.getSex())
                .as("Should not redact sex if hr role with duty of care")
                .isNotNull();
    }

    @Test
    void shouldRedactForNotManagerAndNotHR() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, NON_HR_USER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should redact contact numbers if not manager and not hr")
                .isNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should redact emergency contacts if not manager and not hr")
                .isNull();
        assertThat(actual.getSex())
                .as("Should redact sex if not manager and not hr")
                .isNull();
    }

    @Test
    void shouldRedactForEndManagerInChainNotDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, END_MANAGER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should redact contact numbers if end manager in chain without duty of care")
                .isNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should redact emergency contacts if end manager in chain without duty of care")
                .isNull();
        assertThat(actual.getSex())
                .as("Should redact sex if end manager in chain without duty of care")
                .isNull();
    }

    @Test
    void shouldRedactForHRAndNotDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, HR_USER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should redact contact numbers if hr role without duty of care")
                .isNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should redact emergency contacts if hr role without duty of care")
                .isNull();
        assertThat(actual.getSex())
                .as("Should redact sex if hr role without duty of care")
                .isNull();
    }

    @Test
    void shouldRedactForNotManagerAndNotHRAndNotDutyOfCare() {
        // Given - Nothing

        // When
        Employee actual = DUTY_OF_CARE_RULE.apply(testEmployee, NON_HR_USER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual.getContactNumbers())
                .as("Should redact contact numbers if not manager, not hr role and not duty of care purpose")
                .isNull();
        assertThat(actual.getEmergencyContacts())
                .as("Should redact emergency contacts if not manager, not hr role and not duty of care purpose")
                .isNull();
        assertThat(actual.getSex())
                .as("Should redact sex if not manager, not hr role and not duty of care purpose")
                .isNull();
    }
}
