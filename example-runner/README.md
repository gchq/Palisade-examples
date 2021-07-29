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

# Example Runner
Contains the code and shell scripts used in the running of the examples.

The runner is a wrapper intended to run a set of usecases though the deployed Palisade services.
It will work with [AkkaClient](https://doc.akka.io/docs/akka-http/current/client-side/index.html) to send in requests to Palisade service and collect the response.  

For deployment specific instructions on how to run the example please refer to the following documentation:
- [Local JVM](../deployment-jvm/local-jvm/README.md) - Runs the example in separate JVMs on the local machine
- [Local Kubernetes](../deployment-k8s/local-k8s/README.md) - Runs the example in Kubernetes on the local machine

The full explanation of how this library is used in context to the example is described in the [Overview of the Example](./README.md).
