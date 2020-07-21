<!--
 Copyright 2020 Crown Copyright
 
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

# Local JVM Example

This example demonstrates different users querying an avro file over a REST api running locally in JVMs.

The example runs different queries by different users, with different purposes.
When you run the example you will see the data has been redacted in line with the rules.
For an overview of the example, see [here](../../README.md).

In order to successfully run the Local JVM example, please make sure the [Palisade-services](https://github.com/gchq/Palisade-services) repository has been cloned from GitHub to the intended project location.
A parent directory should at minimum hold the repos [Palisade-services](https://github.com/gchq/Palisade-services) and [Palisade-examples](https://github.com/gchq/Palisade-examples), but [Palisade-common](https://github.com/gchq/Palisade-common), [Palisade-readers](https://github.com/gchq/Palisade-readers) and [Palisade-clients](https://github.com/gchq/Palisade-clients) may be needed to build maven dependencies.

To run the example locally in JVMs follow these steps (from the root of the project):

1. Do a Maven Install for each cloned repo:
   ```bash
    >> ls
      drwxrwxrwx Palisade-common
      drwxrwxrwx Palisade-readers
      drwxrwxrwx Palisade-services
      drwxrwxrwx Palisade-clients
      drwxrwxrwx Palisade-examples
    >> for dir in Palisade-{common,readers,services,clients,examples}; do (cd $dir && mvn clean install); done
   ```
 
1. Make sure you are within the Palisade-services directory:  
   *Some `ls` content hidden for demonstration purposes*
   ```bash
   >> ls
      drwxrwxrwx Palisade-common
      drwxrwxrwx Palisade-readers
      drwxrwxrwx Palisade-services
      drwxrwxrwx Palisade-clients
      drwxrwxrwx Palisade-examples
   >> cd Palisade-services
   >> ls
     drwxrwxrwx audit-service
     drwxrwxrwx data-service
     drwxrwxrwx discovery-service
     drwxrwxrwx palisade-service
     drwxrwxrwx policy-service
     drwxrwxrwx resource-service
     drwxrwxrwx services-manager
     drwxrwxrwx user-service
   ```

1. Start the palisade services and run the example using the services manager. See the services-manager README for more info.

   ```bash
   >> java -Dspring.profiles.active=discovery -jar services-manager/target/services-manager-*-exec.jar --manager.mode=run
   >> java -Dspring.profiles.active=example -jar services-manager/target/services-manager-*-exec.jar --manager.mode=run
   ```
   
1. It will take a couple of minutes for the Spring Boot services to start up.  
   The status of this can be checked by going to http://localhost:8083, or by following the output of the services-manager.  
   There should be 7 services in total to register with Eureka:
    - audit-service
    - data-service
    - discovery-service
    - palisade-service
    - policy-service
    - resource-service
    - user-service
    
1. The RestExample example-model runner (in particular, with the *rest* profile from the *application-rest.yaml*) will be run immediately afterwards
    * The stdout and stderr will by default be stored in `Palisade-services/rest-example.log` and `Palisade-service/rest-example.err` respectively.
    
1. Stop the REST services. See the services-manager README for more info.
    
   ```bash
   >> java -Dspring.profiles.active=example -jar services-manager/target/services-manager-*-exec.jar --manager.mode=shutdown
   >> java -Dspring.profiles.active=discovery -jar services-manager/target/services-manager-*-exec.jar --manager.mode=shutdown
   ```

## Bash Scripts

The above steps can be automated using the provided [bash-scripts](./bash-scripts), all of which are intended to be run from the Palisade-examples root directory:

1. Make sure you are within the Palisade-examples directory:  
   ```bash
   >> ls
     drwxrwxrwx Palisade-common
     drwxrwxrwx Palisade-readers
     drwxrwxrwx Palisade-services
     drwxrwxrwx Palisade-clients
     drwxrwxrwx Palisade-examples
   >> cd Palisade-examples
   >> ls
     drwxrwxrwx deployment
     drwxrwxrwx example-library
     drwxrwxrwx example-model
     drwxrwxrwx hr-data-generator
     drwxrwxrwx performance
   ```

1. Run one or more of the available scripts - eg. to run the example and verify its output:
   ```bash
   deployment/local-jvm/example-model/startServices.sh
   deployment/local-jvm/example-model/runFormattedLocalJVMExample.sh | tee deployment/local-jvm/example-model/exampleOutput.txt
   deployment/local-jvm/example-model/verify.sh
   ```