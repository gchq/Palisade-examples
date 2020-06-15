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

package uk.gov.gchq.palisade.example.hrdatagenerator.types;

import uk.gov.gchq.palisade.Generated;

import java.util.Random;
import java.util.StringJoiner;

public class PhoneNumber {
    private static final int MAX_EXTRA_CONTACTS = 3;
    private static final int PHONE_NUMBER_LENGTH = 10; // excluding leading zero

    private String type; // is this a home number, work number, mobile number ...
    private String number;
    private static final String[] DEFAULT_TYPES = new String[]{"Mobile"};
    private static final String[] POSSIBLE_TYPES = new String[]{"Home", "Work", "Work Mobile"};

    public static PhoneNumber[] generateMany(final Random random) {
        int numberOfExtraContacts = random.nextInt(MAX_EXTRA_CONTACTS);
        PhoneNumber[] phoneNumbers = new PhoneNumber[numberOfExtraContacts + 1];
        phoneNumbers[0] = PhoneNumber.generate(random, DEFAULT_TYPES);
        for (int i = 1; i <= numberOfExtraContacts; i++) {
            phoneNumbers[i] = PhoneNumber.generate(random, POSSIBLE_TYPES);
        }
        return phoneNumbers;
    }

    public static PhoneNumber generate(final Random random) {
        return PhoneNumber.generate(random, POSSIBLE_TYPES);
    }

    private static PhoneNumber generate(final Random random, final String[] possibleTypes) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.type = possibleTypes[random.nextInt(possibleTypes.length)];
        phoneNumber.number = String.format("0%0" + PHONE_NUMBER_LENGTH + "d", random.nextInt((int) Math.pow(10, PHONE_NUMBER_LENGTH)));
        return phoneNumber;
    }

    @Generated
    public String getType() {
        return type;
    }

    @Generated
    public void setType(final String type) {
        this.type = type;
    }

    @Generated
    public String getPhoneNumber() {
        return number;
    }

    @Generated
    public void setPhoneNumber(final String phoneNumber) {
        this.number = phoneNumber;
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", PhoneNumber.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("phoneNumber='" + number + "'")
                .toString();
    }
}
