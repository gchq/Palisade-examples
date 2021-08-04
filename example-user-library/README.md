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
Forr the purposes of showing that the User object is easily extensible we have defined an ExampleUser which is a specialisation of the [Palisade-common User class](https://github.com/gchq/Palisade-common/blob/develop/src/main/java/uk/gov/gchq/palisade/user/User.java).
The only difference is that we have added an extra attribute "trainingCourses" which is used to store a set of Enum TrainingCourse values. This means that we can now base some of our data access decisions based on whether the user has recently completed relevant training courses.

The Users that have been defined for this example are as follows:

| UserId   | Auths               | Roles       | Training courses             |
|:---------|:--------------------|:------------|:-----------------------------|
| Alice    | \[public, private\] | \[HR\]      | \[PAYROLL_TRAINING_COURSE\]  |
| Bob      | \[public\]          | \[ESTATES\] |                              |
| Eve      | \[public\]          | \[IT\]      |                              |


This module also contains the Spring boot classes and yaml files to configure the services on bootup. Firstly we have the Example User Configuration and Prepopulation Factory classes. These are used to tell the user service Spring Boot application to pre load into its cache the example user configurations as specified in the application-example-users.yaml file. The reason we need these extra classes is because we have added a new attribute to the example user object which Spring needs to know how to serialise and deserialise that new attribute.

The yaml file is pretty simple and just specifies that you want to use the example user prepopulation beans (population.userProvider: example) rather then any others that may be declared in the [Example Library ApplicationConfiguration](../example-library/src/main/java/uk/gov/gchq/palisade/example/library/config/ApplicationConfiguration.java) and then defines each of the users as stated in the above table (population.users: ...).

```yaml
population:
  userProvider: example
  users:
  - userId: Alice
    auths:
      public
      private
    roles:
      HR
    trainingCourses:
      PAYROLL_TRAINING_COURSE
  - userId: Bob
    auths:
      public
    roles:
      ESTATES
  - userId: Eve
    auths:
      public
    roles:
      IT
```