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

# Local Kubernetes Example

This example demonstrates different users querying an avro file over a REST api running locally in Kubernetes containers.

The example runs different queries by different users, with different purposes.
When you run the example you will see the data has been redacted in line with the rules.
For an overview of the example, see [here](../../README.md).

In order to successfully run the K8s example, please make sure the [Palisade-services](https://github.com/gchq/Palisade-services) and [example-model](../../example-model) docker images have been built.

### Prerequisites for running in kubernetes 
As well as Docker, this example also requires Kubernetes and Helm 3.
Kubernetes is now bundled as part of Docker.

Windows Subsystem for Linux (WSL) users may have to make special considerations to ensure local directories are mounted correctly, see the [Palisade-services](https://github.com/gchq/Palisade-services) README.

To run the example in a local Kubernetes cluster, follow these steps (from the root [Palisade-examples](../..) directory):

1. Compile the code:
    ```bash
    mvn clean install
    ```

1. Deploy the example (and services):
    ```bash
    helm dep up
    helm upgrade --install palisade . \
    --set global.persistence.dataStores.palisade-data-store.local.hostPath=$(pwd)/resources/data, \
    --set global.persistence.classpathJars.local.hostPath=$(pwd)/deployment/target
    ```

    You can check the pods are available:
    ```bash
    kubectl get pods
    ```

1. Run the test example with:
    ```bash
    kubectl exec example-model-xxxxx -- java -Dspring.profiles.active=k8s,rest -jar /usr/share/example-model/example-model.jar
    ```

1. Delete the deployed services:
    ```bash
    helm delete palisade
    ```
   
You can also use the bash scripts included in the deployment/local-k8s folder as instructed below.

## Bash Scripts

Ensure Line Endings are correct for the environment you are using. If running on Windows, checked out in CRLF, you need to run using WSL and therefore require LF line endings.  
The above steps can be automated using the provided [local bash-scripts](./local-bash-scripts), which are intended to be run from the Palisade-examples root directory.
These, in turn, will call the scripts in [k8s bash-scripts](./k8s-bash-scripts), which are intended to be run from the /usr/share/example-model directory inside the k8s pod:

1. Make sure you are within the Palisade-examples directory:  
   ```bash
   >> ls
     drwxrwxrwx deployment
     drwxrwxrwx example-library
     drwxrwxrwx example-model
     drwxrwxrwx hr-data-generator
     drwxrwxrwx performance
   ```

2. To deploy the example, run:
   ```bash
   bash deployment/local-k8s/local-bash-scripts/deployServicesToK8s.sh
   ```
   You can check the pods are available:
   ```bash
   kubectl get pods
   ```
   
3. After the pods have started, you can run the example, either choosing formatted or unformatted by running the relevant bash script:
   ```bash
   bash deployment/local-k8s/local-bash-scripts/runFormattedK8sExample.sh
   <or>
   bash deployment/local-k8s/local-bash-scripts/runK8sExample.sh
   ```
   
4. If you have run the Formatted example, and want to verify that everything has run as expected, Palisade has a validation script:
    ```bash
   bash deployment/local-k8s/local-bash-scripts/verify.sh
    ```
