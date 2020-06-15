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

public class BankDetails {
    private static final int SORT_CODE_DIGITS = 6;
    private static final int ACCOUNT_NUMBER_DIGITS = 8;
    private String sortCode;
    private String accountNumber;

    public static BankDetails generate(final Random random) {
        BankDetails bankDetails = new BankDetails();
        bankDetails.sortCode = String.format("%0" + SORT_CODE_DIGITS + "d", random.nextInt((int) Math.pow(10, SORT_CODE_DIGITS)));
        bankDetails.accountNumber = String.format("%0" + ACCOUNT_NUMBER_DIGITS + "d", random.nextInt((int) Math.pow(10, ACCOUNT_NUMBER_DIGITS)));
        return bankDetails;
    }

    @Generated
    public String getSortCode() {
        return sortCode;
    }

    @Generated
    public void setSortCode(final String sortCode) {
        this.sortCode = sortCode;
    }

    @Generated
    public String getAccountNumber() {
        return accountNumber;
    }

    @Generated
    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", BankDetails.class.getSimpleName() + "[", "]")
                .add("sortCode='" + sortCode + "'")
                .add("accountNumber='" + accountNumber + "'")
                .toString();
    }
}
