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
    helm upgrade --install palisade .
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

## Bash Scripts

The above steps can be automated using the provided [local bash-scripts](./local-bash-scripts), which are intended to be run from the Palisade-examples root directory.
These, in turn, will call the scripts in [k8s bash-scripts](./k8s-bash-scripts), which are intended to be run from the /usr/share/example-model directory inside the k8s pod:

1. Make sure you are within the Palisade-examples directory:  
   ```bash
   >> ls
     drwxrwxrwx deoloyment
     drwxrwxrwx example-library
     drwxrwxrwx example-model
     drwxrwxrwx hr-data-generator
     drwxrwxrwx performance
   ```

1. Run one or more of the available scripts - eg. to run the example and verify its output:
   ```bash
   chmod +x deployment/local-k8s/*/*.sh
   deployment/local-k8s/local-bash-scripts/deployServicesToK8s.sh
   deployment/local-k8s/local-bash-scripts/runFormattedK8sExample.sh
   deployment/local-k8s/local-bash-scripts/verify.sh
   ```
