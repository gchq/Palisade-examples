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

# Example Model

### Rest Example
The example runs the [uk.gov.gchq.palisade.example.model.runner.RestExample class](/example-model/src/main/java/uk/gov/gchq/palisade/example/model/runner/RestExample.java) which executes several different queries by the different users, with different purposes.
When you run the example you will see the data has been redacted in line with the policy set out in the rules above.

For deployment specific instructions on how to run the example see the [deployment module](/deployment), in particular:  
- [Local JVM](/deployment/local-jvm/README.md) - Runs the example in separate JVMs on the local machine 
- [Local Kubernetes](/deployment/local-k8s/README.md) - Runs the example in Kubernetes on the local machine

### Bulk Retrieval Test (developer/maintenance only)

There is a test script built into the deployment directory for testing how many resources Palisade can retrieve in one request
without experiencing an error. This is intended for developers and maintainers of Palisade only.

This script will create the given number of files in a data directory (while backing up the original one) and then trying to
send one request to retrieve all the resources in that directory.

To run it:

1. Compile the code:
    ```bash
    mvn clean install
    ```

1.  Launch the services:
    ```bash
      ./deployment/local-jvm/bash-scripts/startServices.sh
    ```

1. From a separate terminal run the launch script:

    ```bash
    ./example/deployment/local-jvm/bash-scripts/attemptBulkTest.sh <number of resources>
    ```
