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

package uk.gov.gchq.palisade.example.library.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.gchq.palisade.example.library.common.Purpose;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.example.library.data.DutyOfCareRule;
import uk.gov.gchq.palisade.service.policy.common.Context;
import uk.gov.gchq.palisade.service.policy.common.user.User;
import uk.gov.gchq.palisade.service.policy.common.user.UserId;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


class DutyOfCareTest {

    private static final User FIRST_MANAGER = new User().userId(new UserId().id("1962720332")).roles("Not HR"); // Start of chain and not in HR
    private static final User MIDDLE_MANAGER = new User().userId(new UserId().id("1816031731")).roles("Not HR"); // Middle of chain and not HR
    private static final User END_MANAGER = new User().userId(new UserId().id("1501105288")).roles("Not HR"); // End of chain and not HR
    private static final User HR_USER = new User().userId(new UserId().id("1")).roles(Role.HR.name()); // Not in chain and HR
    private static final User NON_HR_USER = new User().userId(new UserId().id("1")).roles("Not HR"); // Not in chain and not HR
    private static final DutyOfCareRule DUTY_OF_CARE_RULE = new DutyOfCareRule();
    private static final Context DUTY_OF_CARE_CONTEXT = new Context().purpose(Purpose.DUTY_OF_CARE.name());
    private static final Context NOT_DUTY_OF_CARE_CONTEXT = new Context().purpose("Not Duty of Care");

    private Employee testEmployee;

    @BeforeEach
    public void setUp() {
        testEmployee = Employee.generate(new Random(2));
        testEmployee.getManager()[0].setUid(FIRST_MANAGER.getUserId().getId());
        testEmployee.getManager()[0].getManager()[0].setUid(MIDDLE_MANAGER.getUserId().getId());
        testEmployee.getManager()[0].getManager()[0].getManager()[0].setUid(END_MANAGER.getUserId().getId());
    }

    @Test
    void testRedactForStartOfManagerInChain() {
        // Given - Employee, Role, Reason

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, FIRST_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if the first manager is in chain, contactNumbers, emergencyContacts and sex are not redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .isNotNull();
    }

    @Test
    void testRedactForMiddleManagerInChain() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, MIDDLE_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if the middle manager is in chain, contactNumbers, emergencyContacts and sex are not redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .isNotNull();
    }

    @Test
    void testRedactForEndManagerInChain() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, END_MANAGER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if the end manager is in chain, contactNumbers, emergencyContacts and sex are not redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .isNotNull();
    }

    @Test
    void testRedactForHRAndDutyOfCare() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, HR_USER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if the hr role is duty of care, contactNumbers, emergencyContacts and sex are not redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .isNotNull();
    }

    @Test
    void testRedactForNotManagerAndNotHR() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, NON_HR_USER, DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if there is no manager or hr, contactNumbers, emergencyContacts and sex are redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .containsOnlyNulls();
    }

    @Test
    void testRedactForEndManagerInChainNotDutyOfCare() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, END_MANAGER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if the end manager is in the chain without duty of care, contactNumbers, emergencyContacts and sex are redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .containsOnlyNulls();
    }

    @Test
    void testRedactForHRAndNotDutyOfCare() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, HR_USER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if the hr role is not duty of care, contactNumbers, emergencyContacts and sex are redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .containsOnlyNulls();
    }

    @Test
    void testRedactForNotManagerAndNotHRAndNotDutyOfCare() {
        // Given - Nothing

        // When
        var actual = DUTY_OF_CARE_RULE.apply(testEmployee, NON_HR_USER, NOT_DUTY_OF_CARE_CONTEXT);

        // Then
        assertThat(actual)
                .as("Check that if there is no manager or hr with duty of care, contactNumbers, emergencyContacts and sex are redacted ")
                .extracting("contactNumbers", "emergencyContacts", "sex")
                .containsOnlyNulls();
    }
}
