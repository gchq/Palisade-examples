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

# Local JVM Examples

This example demonstrates different users querying an avro file over a REST api running locally in JVMs.

The example runs different queries by different users, with different purposes.
When you run the example, you will see the data has been redacted in line with the rules.
For an overview of the [example data](../../hr-data-generator/README.md) and [example policies](../../example-library/README.md), see the [repo root](../../README.md).

In order to successfully run the Local JVM example, please make sure the [Palisade-services](https://github.com/gchq/Palisade-services) repository has been cloned from GitHub to the intended project location.
A parent directory should at minimum hold the repos [Palisade-services](https://github.com/gchq/Palisade-services) and [Palisade-examples](https://github.com/gchq/Palisade-examples), but [Palisade-common](https://github.com/gchq/Palisade-common), [Palisade-readers](https://github.com/gchq/Palisade-readers) and [Palisade-clients](https://github.com/gchq/Palisade-clients) may be needed to build maven dependencies.

To run the example locally in JVMs, follow these steps (running commands from the root [Palisade-examples](../..) directory):

## Prerequisites

1. Start **Kafka** and **Redis** on localhost using default ports by your preferred means:
   * Try the [docker-compose](./docker-compose.yaml) file if you have docker available - `docker-compose -f ./docker-compose.yml up -d`  
     ***or***
   * Kafka must be listening to `http://localhost:9092`, see the [Kafka Quickstart Guide](https://kafka.apache.org/quickstart)
   * Redis must be listening to `http://localhost:6379`, see the [Redis Quickstart Guide](https://redis.io/topics/quickstart)


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
     drwxrwxrwx attribute-masking-service
     drwxrwxrwx audit-service
     drwxrwxrwx data-service
     drwxrwxrwx filtered-resource-service
     drwxrwxrwx palisade-service
     drwxrwxrwx policy-service
     drwxrwxrwx resource-service
     drwxrwxrwx services-manager
     drwxrwxrwx topic-offset-service
     drwxrwxrwx user-service
   ```

## Running the Examples

### Running using the Bash Scripts

The above steps can be automated using the provided , all of which are intended to be run from the Palisade-examples root directory:

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
     drwxrwxrwx deployment-jvm
     drwxrwxrwx example-library
     drwxrwxrwx example-runner
     drwxrwxrwx performance
   ```

1. Run one or more of the available scripts.

#### Rest Example ([example-runner](../../example-runner/README.md))
To run the example and verify its output, use the [local-jvm example-runner scripts](local-jvm/example-runner):
```bash
deployment-jvm/local-jvm/example-runner/startServices.sh
deployment-jvm/local-jvm/example-runner/runFormattedLocalJVMExample.sh | tee deployment-jvm/local-jvm/example-runner/exampleOutput.txt
deployment-jvm/local-jvm/example-runner/verify.sh
deployment-jvm/local-jvm/example-runner/stopServices.sh
```

#### Performance Tests ([performance](../../performance/README.md))
To run the performance tests, use the [local-jvm performance scripts](performance):
```bash
deployment-jvm/local-jvm/performance/startServices.sh
deployment-jvm/local-jvm/performance/runJVMPerformanceTest.sh | tee deployment-jvm/local-jvm/example-runner/exampleOutput.txt
deployment-jvm/local-jvm/performance/stopServices.sh
```


### Running using the [Services Manager](https://github.com/gchq/Palisade-services/tree/develop/services-manager)
See the [services-manager/README](https://github.com/gchq/Palisade-services/tree/develop/services-manager/README.md) for more info.

#### Rest Example ([example-runner](../../example-runner/README.md))
When using the services-manager, follow these steps (running commands from anywhere under the root [Palisade-services](https://github.com/gchq/Palisade-services) directory):

1. Start the palisade services and run the example using the services manager.
   ```bash
   >> java -Dspring.profiles.active=example-runner -jar services-manager/target/services-manager-*-exec.jar --manager.mode=run
   ```
   
1. It will take a couple of minutes for the Spring Boot services to start up.  
   The status of this can be checked by following the output of the services-manager.  
   There should be 9 services in total running in separate JVM instances:
    - attribute-masking-service
    - audit-service
    - data-service
    - filtered-resource-service
    - palisade-service
    - policy-service
    - resource-service
    - topic-offset-service
    - user-service
    
1. The RestExample example-runner runner (in particular, with the *rest* profile from the *application-rest.yaml*) will be run immediately afterwards
    * The stdout and stderr will by default be stored in `Palisade-services/rest-example.log` and `Palisade-service/rest-example.err` respectively.  
    
   There will briefly be 10 JVM instances running during the example:
   - attribute-masking-service
   - audit-service
   - data-service
   - *example-runner*
   - filtered-resource-service
   - palisade-service
   - policy-service
   - resource-service
   - topic-offset-service
   - user-service

1. Stop the services.
   ```bash
   >> java -Dspring.profiles.active=example-runner -jar services-manager/target/services-manager-*-exec.jar --manager.mode=shutdown
   ```

#### Performance Tests ([performance](../../performance/README.md))
Run as above, but substitute using the `example-runner` profile for the `example-perf` profile

1. Create the performance test data, start the palisade services and run the performance tests using the services manager.
   ```bash
   >> java -Dspring.profiles.active=example-perf -jar services-manager/target/services-manager-*-exec.jar --manager.mode=run --manager.schedule=performance-create-task,palisade-task,performance-test-task
   ```
   
1. It will take a couple of minutes to generate the performance test data.
   It will then take a couple of minutes for the Spring Boot services to start up.  
   The status of this can be checked by following the output of the services-manager.  
   There should be 9 services in total running in separate JVM instances:
   - attribute-masking-service
   - audit-service
   - data-service
   - filtered-resource-service
   - palisade-service
   - policy-service
   - resource-service
   - topic-offset-service
   - user-service

1. The performance runner will be run immediately afterwards
    * The stdout will by default be stored in `Palisade-services/performance-test.log`.  

   There will briefly be 10 JVM instances running during the performance-test:
   - attribute-masking-service
   - audit-service
   - data-service
   - filtered-resource-service
   - palisade-service
   - *performance*
   - policy-service
   - resource-service
   - topic-offset-service
   - user-service

1. Stop the services.
    
   ```bash
   >> java -Dspring.profiles.active=example-perf -jar services-manager/target/services-manager-*-exec.jar --manager.mode=shutdown
   ```
