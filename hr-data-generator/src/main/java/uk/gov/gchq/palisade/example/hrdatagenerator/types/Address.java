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

package uk.gov.gchq.palisade.example.hrdatagenerator.types;

import com.github.javafaker.Faker;

import uk.gov.gchq.palisade.Generated;

import java.util.Random;
import java.util.StringJoiner;

public class Address {

    private String streetAddressNumber;
    private String streetName;
    private String city;
    private String state;
    private String zipCode;

    public static Address generate(final Faker faker, final Random random) {
        Address address = new Address();
        com.github.javafaker.Address fakeAddress = faker.address();
        address.setStreetAddressNumber(fakeAddress.streetAddressNumber());
        address.setStreetName(fakeAddress.streetName());
        address.setCity(fakeAddress.city());
        address.setState(fakeAddress.state());
        address.setZipCode(fakeAddress.zipCode());
        return address;
    }

    @Generated
    public String getStreetAddressNumber() {
        return streetAddressNumber;
    }

    @Generated
    public void setStreetAddressNumber(final String streetAddressNumber) {
        this.streetAddressNumber = streetAddressNumber;
    }

    @Generated
    public String getStreetName() {
        return streetName;
    }

    @Generated
    public void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    @Generated
    public String getCity() {
        return city;
    }

    @Generated
    public void setCity(final String city) {
        this.city = city;
    }

    @Generated
    public String getState() {
        return state;
    }

    @Generated
    public void setState(final String state) {
        this.state = state;
    }

    @Generated
    public String getZipCode() {
        return zipCode;
    }

    @Generated
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", Address.class.getSimpleName() + "[", "]")
                .add("streetAddressNumber='" + streetAddressNumber + "'")
                .add("streetName='" + streetName + "'")
                .add("city='" + city + "'")
                .add("state='" + state + "'")
                .add("zipCode='" + zipCode + "'")
                .toString();
    }
}
