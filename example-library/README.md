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

# Example Library

This module contains the Spring boot application configuration class, which overrides some of the user pre-population beans to enable use to eextend the user attributes that we hold and make use of them in our example data access policies. It also contains the application [configuration yaml files](/src/main/resources/application-example.yaml) to configure the example under the various deployment environments, such that the Users, Resources, Policies and serialisers are all setup correctly.

Each of the pre-population values are loaded into the appropriate service on service start-up, each type is configured by separate yaml files located by the standard example profile.
Populating the data files for the example will rely on the use of the [Synthetic Data Generator](https://github.com/gchq/synthetic-data-generator).