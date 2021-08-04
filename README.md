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

# Palisade Examples

## Status
<span style="color:red">Palisade is no longer under active development</span>

Windows is not an explicitly supported environment, although where possible Palisade has been made compatible.  
For Windows developer environments, we recommend setting up [WSL](https://docs.microsoft.com/en-us/windows/wsl/).

For an overview of Palisade, start with the Palisade introduction and the accompanying guides: QuickStart Guide; and Developer Guide which are found in the [Palisade README](https://github.com/gchq/Palisade/README.md).

## Overview of the Example
This repository contains the business logic required to configure and deploy Palisade to meet your organisations requirements. Therefore the contents of this repository shows the potential of Palisade via an example scenario and performance test suite.

This example is designed around a generic fictional company where staff need to access [sensitive data](https://github.com/gchq/synthetic-data-generator/tree/v0.0.4) for legitimate purposes.
The generic use cases explored in these examples all involve the same HR employee data but with different outcomes based on the rules that are enforced.

Alice works in HR and has completed the Payroll Training Course.  
She needs to perform different tasks, one of which is to process the payroll.
To process the payroll she will need to have access to bank details however, she should not have access to contact information.  
As a member of the HR department she needs to view the employee contact information to meet the legal obligation for duty of care, so for this purpose she will be able to view the contact information, but the bank information will be redacted. 

Bob works in Estates and has not completed any formal training.
His work includes the need to plan the companies office locations and staff parking requirements.  
Therefore, he needs to know the rough location of staff home addresses, but does not need personal information such as the contact or banking information.  
Bob is also a line manager and in that capacity needs to perform queries to address any duty of care concern for the employees under his management.
In this capacity, he will need to view the contact information, including the emergency contact number, and the exact postal code. However, he does not need to view the payroll information.

Eve works in the IT department and has not completed any training.
She does not have a legitimate need to query the HR employee data.
The response for her is that she will not be able to access any of the HR employee data. 

For more detail on the technical aspect of the users in the example see the [example-user-library](./example-user-library/README.md).  
For more detail on the technical aspect of the rules and purposes that are used in the example see the [example-rule-library](./example-rule-library/README.md).


## Deployment of Palisade Examples

To demonstate the flexibility of Palisade we have been developing and testing Palisade under 3 deployment environments:
- [local-JVM](./deployment-jvm/local-jvm/README.md)
- [local-K8s](./deployment-k8s/local-k8s/README.md)
- [aws-K8s](./deployment-k8s/aws-k8s/README.md)

These three environments show that Palisade can work both on a single machine laptop as well as in a production grade cloud Kubernetes environment all without having to modify the core service code that is based in the [Palisade-services](https://github.com/gchq/Palisade-services) repository.

To demonstrate the flexibility of what rules and data we can work against we have the [example runner](./deployment-k8s/local-k8s/example-runner) and the [performance suite](./performance/README.mde), which operate on different files and have different data access policies set in the spring boot configuration.

## Structure of Examples repository
The Palisade-examples repository is broken down into the following specific modules:

### [Example User Library](./example-user-library/README.md)
Contains the business logic related to the users, the roles and purposes they work under.
The users in the example have different job roles and need access to the employee data records with different purposes.
Each of these users is defined by the ExampleUser which is a specialisation of the Palisade-common User class.

### [Example Rule Library](./example-rule-library/README.md)
Contains the busineess logic of the data access rules and therefore how those rules apply to the resources and user attributes.
The rules are split into two categories: resource-level rules which apply to an entire resource; and the record-level rules which apply to the individual records.
They are provided to illustrate the rules a company might set out for users accessing sensitive employee data, depending upon their role in the company.
Normally the source of these rules would be provided by the Policy Service, but for this example they are a provided in a pre-populated data set that is used to create the Rules used in the example.
Concrete implementation of the rules are a specialisation of the Palisade-common Rule interface.

### [Example Library](./example-library/README.md)
The Example Library is primarily a factory that uses the [Example User Library](./example-user-library/README.md) and the [Example Rule Library](./example-rule-library/README.md) to spin-up the users, rules and the data needed for running the example.

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
