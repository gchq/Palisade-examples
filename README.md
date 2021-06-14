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
# Examples

The example demonstrates different users querying an avro file over a REST api. 
These examples are run in 3 different scenarios as described in the [Deployment-k8s](deployment-k8s) and [Deployment-jvm](deployment-jvm) modules:
- [local-jvm](./deployment-jvm/local-jvm/README.md)
- [local-k8s](./deployment-k8s/local-k8s/README.md)
- [aws-k8s](./deployment-k8s/aws-k8s/README.md)

The rest of the modules contain all the necessary classes and objects required for running the examples in those environments.

The Palisade-examples repository contains all the example specific modules as follows:

### [Deployment-k8s](deployment-k8s)
Contains the deployment specific code and scripts for running the example within a Kubernetes environment. 
Current deployment targets are:
* [AWS Docker/Kubernetes Containers](./deployment-k8s/aws-k8s/README.md)
* [Local Docker/Kubernetes Containers](./deployment-k8s/local-k8s/README.md)

### [Deployment-jvm](deployment-jvm)
Contains the deployment specific code and scripts for running the example within a local JVM environment.
These are intended to be run locally, but there are provided dockerfiles and charts to run in K8s.
* [Local JVM Processes](./deployment-jvm/local-jvm/README.md)

### [Example Library](example-library/README.md)
The Example Library is a collection of Java classes that are required for running the example. 
For example, the `ExampleUser` class, as well as a number of fine-grained and coarse-grained policies that will be applied to the returned records.
The `ExampleUser` datatype is a specialisation of the [Palisade-common](https://github.com/gchq/Palisade-common) `User` class and contains specific extra fields. 
The policies applied when running the example are a collection of static coarse-grained (resource-level) and fine-grained (record-level) rules that can be found in the [Example Library](example-library/README.md), and are used for pre-populating the Palisade services with data.
This collection of example-specific rules, types and configurations is based around the possible policies a company might set out for users accessing sensitive employee data, depending upon their role in the company.

### [Example Runner](example-runner/README.md)
The example scenarios can be run using the Spring Boot REST client application in the *Example Runner*, requesting and reading for a number of different users, resources and contexts.
This simple Spring Boot application has a number of different runners for such REST clients, which also manage using the palisade-service response to connect to the appropriate data-service.

### [Performance](performance/README.md)
The performance of the palisade services compared to native file reads (and other metrics) can be measured using the *Performance* testing suite.
This performance-testing suite has a number of different trial types, datasets and policy variants to cover a reasonable number of common use-cases.
