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

Otherwise, follow the [local-jvm prerequisites](../../deployment-jvm/local-jvm/README.md).


## Deployment Types
On running `helm install`, there are a number of variables that can be set in the [values.yaml](values.yaml).
In particular, the `global.deployment` field controls what sort of services and configurations are deployed.

### `example` Deployment
* Deploy a pair of `employee_fileN.avro` files to the Data Service.
* Prepopulate the services' caching layers, or otherwise configure them, with users, resources and serialisers matching the data files, and policies.
* Deploy an `example-runner` pod.

These resources are requested by the smoketest (using the `example-runner` pod).
Be aware that most data only exists in a cache and has a TTL - when this cache expires, the data will be gone until redeploying.

### `performance-test` Deployment
* Deploy hundreds of `employee_fileN.avro` files to the Data Service.
* Prepopulate the services' caching layers, or otherwise configure them, with users, resources matching the data files, and policies.
* Deploy a `performance` pod.

The performance test measures the time to request and read resources in a synthetic scenario.

### `s3` Deployment
* Deploy the [S3 Resource Service](https://github.com/gchq/Palisade-readers/tree/develop/s3-resource) and [S3 Data Reader](https://github.com/gchq/Palisade-readers/tree/develop/s3-reader) modules.
* Prepopulate the services' caching layers, or otherwise configure them, with users, serialisers, and policies only.

This allows requesting and reading resources using an S3 URI, which follows the scheme `s3://bucketname/objectprefix`.

You must additionally supply your AWS S3 credentials in the `values.yaml` as follows (substituting for the appropriate values):
```yaml
global:
  env:
    s3:
    - name: "SPRING_PROFILES_ACTIVE"
      value: "k8s,s3,example-s3"
    - name: "AWS_ACCESS_KEY_ID"
      value: "your-access-key-id-here"
    - name: "AWS_SECRET_ACCESS_KEY"
      value: "your-secret-access-key-here"
    - name: "AWS_SESSION_TOKEN"
      value: "your-session-token-here"
    - name: "AWS_REGION"
      value: "your-aws-region-here"
```

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
     drwxrwxrwx deployment-jvm
     drwxrwxrwx example-library
     drwxrwxrwx example-runner
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
     drwxrwxrwx deployment-jvm
     drwxrwxrwx example-library
     drwxrwxrwx example-runner
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
