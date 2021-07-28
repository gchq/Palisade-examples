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
# <img src="logos/logo.svg" width="180">

## A Tool for Complex and Scalable Data Access Policy Enforcement
Windows is not an explicitly supported environment, although where possible Palisade has been made compatible.  
For Windows developer environments, we recommend setting up [WSL](https://docs.microsoft.com/en-us/windows/wsl/).

# Palisade Examples
The example demonstrates different users querying an avro file over a REST api. 
These examples are run in 3 different scenarios as described in the [Deployment-K8s](./deployment-k8s/README.md) and [Deployment-JVM](./deployment-jvm/README.md) modules:
- [local-JVM](./deployment-jvm/local-jvm/README.md)
- [local-K8s](./deployment-k8s/local-k8s/README.md)
- [aws-K8s](./deployment-k8s/aws-k8s/README.md)

The rest of the modules contain all the necessary classes and objects required for running the examples in those environments.

The Palisade-examples repository contains all the example specific modules as follows:

### [Example User Library](./example-user-library/README.md)
Users and supporting code for the example.  
The users in the example have different job roles and need access to the employee data records each with a different purpose.
Each of these users is defined by the ExampleUser which is a specialisation of the [Palisade-common](https://github.com/gchq/Palisade-common/README.md) User class.

### [Example Rule Library](./example-rule-library/README.md)
Rules and supporting code that are to be used in the example.
The rules are split into two categories: resource-level rules which apply to an entire resource; and the record-level rules which apply to the individual records.
They are provided to illustrate the rules a company might set out for users accessing sensitive employee data, depending upon their role in the company.
Normally the source of these rules would be provided by the Policy Service, but for this example they are a provided in a pre-populated data set that is used to create the Rules used in the example.
Concrete implementation of the rules are a specialisation of the [Palisade-common](https://github.com/gchq/Palisade-common/README.md) Rule interface.

### [Example Library](./example-library/README.md)
The Example Library is primarily a factory that uses the [Example User Library](./example-user-library/README.md) and the [Example Rule Library](./example-library/README.md) to spin-up the users, rules and records needed for running the example.

### [Example Runner](./example-runner/README.md)
The example scenarios can be run using the Spring Boot REST client application in the Example Runner, requesting and reading for a number of different users, resources and contexts.
This simple Spring Boot application has a number of different runners for such REST clients, which also manage using the Palisade Service response to connect to the appropriate Data Service.

### [Performance](./performance/README.md)
The performance of the Palisade services compared to native file reads (and other metrics) can be measured using the Performance testing suite.
This performance-testing suite has a number of different trial types, datasets and policy variants to cover a reasonable number of common use-cases.

### [Deployment-K8s](./deployment-k8s/README.md)
Contains the deployment specific code and scripts for running the example within a Kubernetes environment.
Current deployment targets are:
* [AWS Docker/Kubernetes Containers](./deployment-k8s/aws-k8s/README.md)
* [Local Docker/Kubernetes Containers](./deployment-k8s/local-k8s/README.md)

### [Deployment-JVM](./deployment-jvm/README.md)
Contains the deployment specific code and scripts for running the examples within a local JVM environment.
Shell scripts are provided to set-up and run both the example and the performance tests.
Also, Dockerfiles and charts are provided to run all the JVMs in a single container.
* [Local JVM Processes](./deployment-jvm/local-jvm/README.md)

## Overview of the Example
The following describes the key aspects of the example and explains how the different users will see the same set of data records based on the applied rules.

##ExampleUser
The example deals with the following users:
- User Alice has the role HR and completed the PAYROLL_TRAINING_COURSE
- User Bob has the role ESTATES and not completed any training
- User Eve has the role IT and not completed any training

### BankDetailsRule
The bankDetails field should be returned:
- if the user querying the file has the HR role, completed the PAYROLL_TRAINING_COURSE, and the purpose of the query is SALARY

In all other cases the bankDetails field should be redacted.

### DutyOfCareRule
This rule is concerned with the contactNumber, emergencyContacts and sex fields. These fields should be returned:
- if the user querying the file has the HR role, and the purpose of the query is DUTY_OF_CARE
- if the user querying the file is the line manager of the Employee record being queried, and the purpose of the query is DUTY_OF_CARE

In all other cases these fields should be redacted.

### FirstResourceRule
This rule is concerned with the resource file that is being requested:
- if the user has an HR role they will be able to access the first resource file

In all other cases the first resource will not be returned to the user.

### NationalityRule
The nationality field should be returned:
- if the user querying the file has the HR role, and the purpose of the query is STAFF_REPORT

In all other cases the nationality field should be redacted.

### RecordMaskingRule
This rule is concerned with the full record:
- if the user querying the file has the HR role then no modifications are made to the record
- if the user is in the management tree of the employee then no modifications are made to the record
- if the user querying the file has the ESTATES role then the DateOfBirth, HireDate, Grade and Manager fields are redacted

In all other cases the record will have no information returned.

### ZipCodeMaskingRule
This rule is concerned with the address field:
- if the user querying the file has the HR role then the whole address is returned
- if the purpose of the query is DUTY_OF_CARE and the user querying the file is the line manager of the Employee record being queried then the whole address is returned
- if the user querying the file has the ESTATES role then the address field should be returned with the zipcode/postcode masked to reduce its precision

In all other cases the address field should be redacted.
