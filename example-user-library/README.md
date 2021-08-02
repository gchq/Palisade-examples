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
Each of these users is defined as an ExampleUser which is a specialisation of the [Palisade-common User class](https://github.com/gchq/Palisade-common/blob/develop/src/main/java/uk/gov/gchq/palisade/user/User.java).
These are accompanied by key attributes that defined the properties of the users in the working example.
These include:
    Purpose which defines the reason a query is being made and therefore enriches th equery context.
    Role which defines the department that an employee works in
    Training Course which list the formal training course that an employee has completed
With this example of a user, we are able to demonstrate the ease it is to apply the existing concept of a Palisade user to fit the requirements of a business. 

For a full explanation of the motivation behind this library is used in context to the example is described in the [Overview of the Example](./README.md).

This module also contains the Spring boot classes and yaml files to configure the services on bootup. Firstly we have the Example User Configuration and Prepopulation Factory classes. These are used to tell the user service Spring Boot application to pre load into its cache the example user configurations as specified in the application-example-users.yaml file. The reason we need these extra classes is because we have added a new attribute to the example user object which Spring needs to know how to serialise and deserialise that new attribute.
