<!--
Copyright 2018-2021 Crown Copyright

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
 
# HR Data Generator

This module contains the code for the HR Data examples. This includes generator code that can produce AVRO files of
synthetic HR data.

The hr-data-generator supplies a synthetic set of data, constructing a number of `Employee` objects with the following fields:
```
class Employee {
    UserId uid;
    String name;
    String dateOfBirth;
    PhoneNumber[] contactNumbers;
    EmergencyContact[] emergencyContacts;
    Address address;
    BankDetails bankDetails;
    String taxCode;
    Nationality nationality;
    Manager[] manager;
    String hireDate;
    Grade grade;
    Department department;
    int salaryAmount;
    int salaryBonus;
    WorkLocation workLocation;
    Sex sex;
}
```
The manager field is an array of manager, which could potentially be nested several layers deep, in the generated example manager is nested 3-5 layers deep.

To use the generator navigate to the Palisade-examples directory and run:

```bash
mvn clean install
```

then to start the generator:

```bash
./deployment/hr-data-generator/createHRData.sh PATH EMPLOYEES FILES [THREADS]
```
where:
- PATH is the relative path to generate the files
- EMPLOYEES is the number of employee records to create
- FILES is the number of files to spread them over
- THREADS (optional) specifies the number of threads to use.

For example to generate 1,000,000 employee records, spread over 15 files, running the program with 15 threads, and writing the output files to /data/employee:

```bash
./deployment/hr-data-generator/createHRData.sh /data/employee 1000000 15 15
```
