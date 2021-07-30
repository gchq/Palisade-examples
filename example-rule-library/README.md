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
# <img src="../logos/logo.svg" width="180">

## A Tool for Complex and Scalable Data Access Policy Enforcement

# Example Rule library
This module contains the specific rules that are used when running the example. 
Each of these rules is a specialisation of the Palisade-common [Rule interface](https://github.com/gchq/Palisade-common/blob/develop/src/main/java/uk/gov/gchq/palisade/rule/Rule.java).
There are two types of rule that can be applied to a resource:

1. Resource Level Rules
1. Record Level Rules

The Resource Level Rules are coarse grain rules that will allow a user to access a resource.  
The Record Level Rules are fine grain rules that define what records, within a given resource, the user is allowed to view.

When running the example within a Palisade deployment the resources and policies associated with the example will need to be populated into the cache of the Resource Service and Policy Service respectively.
The information for doing this is contained within application yaml files that are located in the [resources directory](./src/main/resources)

### Resource Level Rules (coarse grain)

#### FirstResourceRule
This rule is concerned with the resource file that is being requested:
- if the user has an HR role they will be able to access the first resource file

In all other cases the first resource will not be returned to the user.

### Record Level Rules (fine grain)

#### BankDetailsRule
The bankDetails field should be returned:
- if the user querying the file has the HR role, completed the PAYROLL_TRAINING_COURSE, and the purpose of the query is SALARY. All of these criteria must be met if the user is to see the employees bank details.

In all other cases the bankDetails field should be redacted.

#### DutyOfCareRule
This rule is concerned with the contactNumber, emergencyContacts and sex fields. These fields should be returned:
- if the user querying the file has the HR role, and the purpose of the query is DUTY_OF_CARE
- if the user querying the file is the management tree of the Employee record being queried, and the purpose of the query is DUTY_OF_CARE

In all other cases these fields should be redacted.

#### NationalityRule
The nationality field should be returned:
- if the user querying the file has the HR role, and the purpose of the query is STAFF_REPORT

In all other cases the nationality field should be redacted.

#### RecordMaskingRule
This rule is concerned with the full record:
- if the user querying the file has the HR role then no modifications are made to the record
- if the user is in the management tree of the employee then no modifications are made to the record
- if the user querying the file has the ESTATES role then the DateOfBirth, HireDate, Grade and Manager fields are redacted

In all other cases the record will have no information returned.

#### ZipCodeMaskingRule
This rule is concerned with the address field:
- if the user querying the file has the HR role then the whole address is returned
- if the purpose of the query is DUTY_OF_CARE and the user querying the file is the line manager of the Employee record being queried then the whole address is returned
- if the user querying the file has the ESTATES role then the address field should be returned with the zipcode/postcode masked to reduce its precision

In all other cases the address field should be redacted.

The full explanation of how this library is used in context to the example is described in the [Overview of the Example](../README.md#Overview of the Example).



