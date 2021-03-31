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
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RecordMaskingTest {

    private static final User FIRST_MANAGER = new User().userId(new UserId().id("1962720332")).roles("Not HR"); // Start of chain and not in HR or Estates
    private static final User MIDDLE_MANAGER = new User().userId(new UserId().id("1816031731")).roles("Not HR"); // Middle of chain and not HR or Estates
    private static final User END_MANAGER = new User().userId(new UserId().id("1501105288")).roles("Not HR"); // End of chain and not HR or Estates
    private static final User HR_USER = new User().userId(new UserId().id("1")).roles(Role.HR.name()); // Not in chain and has HR role
    private static final User ESTATES_USER = new User().userId(new UserId().id("1")).roles(Role.HR.name()); // Not in chain and has Estates role
    private static final User NON_HR_NON_ESTATES_USER = new User().userId(new UserId().id("1")).roles("Not HR"); // Not in chain and not HR or Estates
    private static final Context TEST_CONTEXT = new Context().purpose("A purpose");
    private static final RecordMaskingRule RECORD_MASKING_RULE = new RecordMaskingRule();

    private Employee testEmployee;

    @Before
    public void setUp() {
        testEmployee = Employee.generate(new Random(2));
        testEmployee.getManager()[0].setUid(FIRST_MANAGER.getUserId().getId());
        testEmployee.getManager()[0].getManager()[0].setUid(MIDDLE_MANAGER.getUserId().getId());
        testEmployee.getManager()[0].getManager()[0].getManager()[0].setUid(END_MANAGER.getUserId().getId());
    }

    @Test
    public void noRedactionForFirstLevelManager() {
        //Given - Employee, Role, Reason

        //When
        Employee actual = RECORD_MASKING_RULE.apply(testEmployee, FIRST_MANAGER, TEST_CONTEXT);

        //Then
        assertNotNull("Should not redact record for first manager", actual);
    }

    @Test
    public void noRedactionForMidLevelManager() {
        //Given - Employee, Role, Reason

        //When
        Employee actual = RECORD_MASKING_RULE.apply(testEmployee, MIDDLE_MANAGER, TEST_CONTEXT);

        //Then
        assertNotNull("Should not redact record for middle manager", actual);
    }

    @Test
    public void noRedactionForEndLevelManager() {
        //Given - Employee, Role, Reason

        //When
        Employee actual = RECORD_MASKING_RULE.apply(testEmployee, END_MANAGER, TEST_CONTEXT);

        //Then
        assertNotNull("Should not redact record for end manager", actual);
    }

    @Test
    public void noRedactionForHrRole() {
        //Given - Employee, Role, Reason

        //When
        Employee actual = RECORD_MASKING_RULE.apply(testEmployee, HR_USER, TEST_CONTEXT);

        //Then
        assertNotNull("Should not redact record for hr role", actual);
    }

    @Test
    public void noRedactionForEstatesRole() {
        //Given - Employee, Role, Reason

        //When
        Employee actual = RECORD_MASKING_RULE.apply(testEmployee, ESTATES_USER, TEST_CONTEXT);

        //Then
        assertNotNull("Should not redact record for estates role", actual);
    }

    @Test
    public void redactionForNonManagerUser() {
        //Given - Employee, Role, Reason

        //When
        Employee actual = RECORD_MASKING_RULE.apply(testEmployee, NON_HR_NON_ESTATES_USER, TEST_CONTEXT);

        //Then
        assertNull("Should redact record for non-manager, non-hr, non-estates user", actual);
    }
}
