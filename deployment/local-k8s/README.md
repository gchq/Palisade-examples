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

# Local Kubernetes Examples

This example demonstrates different users querying an avro file over a REST api running locally in Docker/Kubernetes containers.

The example runs different queries by different users, with different purposes.
When you run the example you will see the data has been redacted in line with the rules.
For an overview of the example, see [here](../../README.md).

In order to successfully run the K8s example, please make sure the [Palisade-services](https://github.com/gchq/Palisade-services) and [example-runner](../../example-runner) docker images have been built.

To run the example in a local Kubernetes cluster, follow these steps (running commands from the root [Palisade-examples](../..) directory):

## Prerequisites
As well as Docker, this example also requires Kubernetes and Helm 3.
Kubernetes is now bundled as part of Docker.

Windows Subsystem for Linux (WSL) users may have to make special considerations to ensure local directories are mounted correctly, see the [Palisade-services README](https://github.com/gchq/Palisade-services/tree/develop/README.md).

Otherwise, follow the [local-jvm prerequisites](../local-jvm/README.md).


## Running using the Bash Scripts

Ensure Line Endings are correct for the environment you are using. If running on Windows, checked out in CRLF, be aware that Docker will be expecting LF endings in any scripts inside containers.

Both the [example-runner](../../example-runner) and the [performance tests](../../performance) have two sets of scripts, one local set outside of the cluster ([here](./example-runner) and [here](./performance) respectively) and one set inside the containers ([here](../../example-runner/src/main/resources/k8s-bash-scripts) and [here](../../performance/src/main/resources/k8s-bash-scripts) respectively).
The deployment steps can be automated using the provided local bash scripts, which are intended to be run from the [Palisade-examples](../..) root directory.
These, in turn, will call the scripts in [k8s bash-scripts](../../example-runner/src/main/resources/k8s-bash-scripts), which are intended to be run from the `/usr/share/example-runner` or `/usr/share/performance` directory inside the pod:

### Rest Example ([example-runner](../../example-runner/README.md))
1. Make sure you are within the Palisade-examples directory:  
   ```bash
   >> ls
     drwxrwxrwx deployment
     drwxrwxrwx example-library
     drwxrwxrwx example-runner
     drwxrwxrwx hr-data-generator
     drwxrwxrwx performance
   ```

1. To deploy the example, run:
   ```bash
   bash deployment/local-k8s/example-runner/deployServicesToK8s.sh
   ```
   You can check the pods are available:
   ```bash
   kubectl get pods
   ```
   
1. After the pods have started, you can run the example, either choosing formatted or unformatted by running the relevant bash script:
   ```bash
   bash deployment/local-k8s/example-runner/runFormattedK8sExample.sh
   <or>
   bash deployment/local-k8s/example-runner/runK8sExample.sh
   ```
   
1. If you have run the Formatted example, and want to verify that everything has run as expected, Palisade has a validation script:
    ```bash
   bash deployment/local-k8s/example-runner/verify.sh
    ```

1. Delete the deployed services:
    ```bash
    helm delete palisade
    ```

### Performance Tests ([performance](../../performance/README.md))
1. Make sure you are within the Palisade-examples directory:  
   ```bash
   >> ls
     drwxrwxrwx deployment
     drwxrwxrwx example-library
     drwxrwxrwx example-runner
     drwxrwxrwx hr-data-generator
     drwxrwxrwx performance
   ```

1. Create the performance test dataset locally, run:
   ```bash
   bash deployment/local-k8s/performance/createPerformanceData.sh
   ```

1. To deploy the performance tests, run:
   ```bash
   bash deployment/local-k8s/performance/deployServicesToK8s.sh
   ```
   You can check the pods are available:
   ```bash
   kubectl get pods
   ```
   
1. After the pods have started, you can run the performance tests:
   ```bash
   bash deployment/local-k8s/runK8sPerformanceTest.sh
   ```

1. Delete the deployed services:
    ```bash
    helm delete palisade
    ```
