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

# Example User Library
Contains the ExampleUser and supporting code.
Each of these users is defined by the ExampleUser which is a specialisation of the [Palisade-common](https://github.com/gchq/Palisade-common/README.md) User class.
This will be used to create the following users:
- User Alice has the role HR and completed the PAYROLL_TRAINING_COURSE
- User Bob has the role ESTATES and not completed any training
- User Eve has the role IT and not completed any training

The full explanation of how this library is used in context to the example is described in the [Overview of the Example](./README.md).

