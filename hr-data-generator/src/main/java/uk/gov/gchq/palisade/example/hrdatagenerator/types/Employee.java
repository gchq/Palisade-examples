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

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

import uk.gov.gchq.palisade.Generated;
import uk.gov.gchq.palisade.UserId;
import uk.gov.gchq.palisade.example.hrdatagenerator.utils.DateHelper;

import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;

public class Employee {
    private static final int MIN_MANAGERS = 2;
    private static final int MAX_EXTRA_MANAGERS = 3;
    private static final int MIN_SALARY = 20_000;
    private static final int MAX_EXTRA_SALARY = 100_000;
    private static final int MAX_SALARY_BONUS = 10_000;

    private UserId uid;
    private String name;
    private String dateOfBirth;
    private PhoneNumber[] contactNumbers;
    private EmergencyContact[] emergencyContacts;
    private Address address;
    private BankDetails bankDetails;
    private String taxCode;
    private Nationality nationality;
    private Manager[] manager;
    private String hireDate;
    private Grade grade;
    private Department department;
    private int salaryAmount;
    private int salaryBonus;
    private WorkLocation workLocation;
    private Sex sex;


    public static Employee generate(final Random random) {
        Employee employee = new Employee();
        Faker faker = ThreadLocalFaker.getFaker(random);
        employee.setUid(generateUID(random));
        Name employeeName = faker.name();
        employee.setName(employeeName.firstName() + " " + employeeName.lastName()); // we are storing name as a string not a Name
        employee.setDateOfBirth(DateHelper.generateDateOfBirth(random));
        employee.setContactNumbers(PhoneNumber.generateMany(random));
        employee.setEmergencyContacts(EmergencyContact.generateMany(faker, random));
        employee.setAddress(Address.generate(faker, random));
        employee.setBankDetails(BankDetails.generate(random));
        employee.setTaxCode(generateTaxCode());
        employee.setNationality(Nationality.generate(random));
        employee.setManager(Manager.generateMany(random, MIN_MANAGERS + random.nextInt(MAX_EXTRA_MANAGERS)));
        employee.setHireDate(DateHelper.generateHireDate(employee.dateOfBirth, random));
        employee.setGrade(Grade.generate(random));
        employee.setDepartment(Department.generate(random));
        employee.setSalaryAmount(MIN_SALARY + random.nextInt(MAX_EXTRA_SALARY));
        employee.setSalaryBonus(random.nextInt(MAX_SALARY_BONUS));
        employee.setWorkLocation(WorkLocation.generate(faker, random));
        employee.setSex(Sex.generate(random));

        return employee;
    }

    public static UserId generateUID(final Random random) {
        return new UserId().id(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
    }

    private static String generateTaxCode() {
        return "11500L";
    }

    @Generated
    public UserId getUid() {
        return uid;
    }

    @Generated
    public void setUid(final UserId uid) {
        this.uid = uid;
    }

    @Generated
    public String getName() {
        return name;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @Generated
    public void setDateOfBirth(final String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Generated
    public PhoneNumber[] getContactNumbers() {
        return contactNumbers;
    }

    @Generated
    public void setContactNumbers(final PhoneNumber[] contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    @Generated
    public EmergencyContact[] getEmergencyContacts() {
        return emergencyContacts;
    }

    @Generated
    public void setEmergencyContacts(final EmergencyContact[] emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }

    @Generated
    public Address getAddress() {
        return address;
    }

    @Generated
    public void setAddress(final Address address) {
        this.address = address;
    }

    @Generated
    public BankDetails getBankDetails() {
        return bankDetails;
    }

    @Generated
    public void setBankDetails(final BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }

    @Generated
    public String getTaxCode() {
        return taxCode;
    }

    @Generated
    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    @Generated
    public Nationality getNationality() {
        return nationality;
    }

    @Generated
    public void setNationality(final Nationality nationality) {
        this.nationality = nationality;
    }

    @Generated
    public Manager[] getManager() {
        return manager;
    }

    @Generated
    public void setManager(final Manager[] manager) {
        this.manager = manager;
    }

    @Generated
    public String getHireDate() {
        return hireDate;
    }

    @Generated
    public void setHireDate(final String hireDate) {
        this.hireDate = hireDate;
    }

    @Generated
    public Grade getGrade() {
        return grade;
    }

    @Generated
    public void setGrade(final Grade grade) {
        this.grade = grade;
    }

    @Generated
    public Department getDepartment() {
        return department;
    }

    @Generated
    public void setDepartment(final Department department) {
        this.department = department;
    }

    @Generated
    public int getSalaryAmount() {
        return salaryAmount;
    }

    @Generated
    public void setSalaryAmount(final int salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    @Generated
    public int getSalaryBonus() {
        return salaryBonus;
    }

    @Generated
    public void setSalaryBonus(final int salaryBonus) {
        this.salaryBonus = salaryBonus;
    }

    @Generated
    public WorkLocation getWorkLocation() {
        return workLocation;
    }

    @Generated
    public void setWorkLocation(final WorkLocation workLocation) {
        this.workLocation = workLocation;
    }

    @Generated
    public Sex getSex() {
        return sex;
    }

    @Generated
    public void setSex(final Sex sex) {
        this.sex = sex;
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", Employee.class.getSimpleName() + "[", "]")
                .add("uid=" + uid)
                .add("name='" + name + "'")
                .add("dateOfBirth='" + dateOfBirth + "'")
                .add("contactNumbers=" + Arrays.toString(contactNumbers))
                .add("emergencyContacts=" + Arrays.toString(emergencyContacts))
                .add("address=" + address)
                .add("bankDetails=" + bankDetails)
                .add("taxCode='" + taxCode + "'")
                .add("nationality=" + nationality)
                .add("manager=" + Arrays.toString(manager))
                .add("hireDate='" + hireDate + "'")
                .add("grade=" + grade)
                .add("department=" + department)
                .add("salaryAmount=" + salaryAmount)
                .add("salaryBonus=" + salaryBonus)
                .add("workLocation=" + workLocation)
                .add("sex=" + sex)
                .toString();
    }
}
