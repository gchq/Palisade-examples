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

# AWS Kubernetes Examples

This example demonstrates different users querying an avro file over a REST api running within an Amazon Web Services (AWS) environment in Docker/Kubernetes containers.

The example runs different queries by different users, with different purposes.
When you run the example you will see the data has been redacted in line with the rules.
For an overview of the example, see [here](../../README.md).

### Rest Example ([example-runner](../../example-runner/README.md))

#### Deploying into AWS

Palisade can be deployed into an AWS environment by using the `deployServicesToK8s.sh` script from the local Palisade-examples directory.
When running the script a number of AWS values will have to be passed in as arguments:
```
   -n(amespace)     The name for the K8s namespace to deploy to
   -r(epository)    The handle for the Elastic Container Registry (ECR) repository
   -h(ostname)      The handle for the Elastic Load Balancer (ELB) hostname of the cluster deployment
   -d(atastore)     The handle for the Elastic File System (EFS) aws volume handle used as a data-store
   -c(lasspathjars) The handle for the Elastic File System (EFS) aws volume handle used for storing classpath JARs
   -t(raefik)       A boolean flag for if traefik should be installed
   -p(refix)        The name prefix to use in kafka topics
   -r(ole)          The AWS iam role used to retrieve the relevant AWS credentials
```
```
e.g.
bash deployment/aws-k8s/example-runner/deployServicesToK8s.sh \
    -n namespace-value \
    -r aws-repository-handle \
    -h deployment-hostname-handle \
    -d data-store-efs-handle \
    -c classpath-jars-efs-handle \
    -t false \
    -p namespace-value \
    -r its-user
```

These are then used in the helm command to deploy Palisade to AWS:
```
helm upgrade --install palisade . \
    --set global.hosting=aws \
    --set global.repository= -r value \
    --set global.hostname= -h value \
    --set global.persistence.dataStores.palisade-data-store.aws.volumeHandle= -d value \
    --set global.persistence.classpathJars.aws.volumeHandle= -c value \
    --set global.deployment=example-s3 \
    --set global.kafka.install=true \
    --set global.redis.install=true \
    --set Palisade-services.traefik.install= -t value \
    --set global.topicPrefix= -p value \
    --set global.env.example-s3[0].name="SPRING_PROFILES_ACTIVE" \
    --set global.env.example-s3[0].value="k8s\,\s3\,\example-s3\,\debug" \
    --set global.env.example-s3[1].name="AWS_ACCESS_KEY_ID" \
    --set global.env.example-s3[1].value="${aws configure get aws_access_key_id}" \
    --set global.env.example-s3[2].name="AWS_SECRET_ACCESS_KEY" \
    --set global.env.example-s3[2].value="${aws configure get aws_secret_access_key}" \
    --set global.env.example-s3[3].name="AWS_SESSION_TOKEN" \
    --set global.env.example-s3[3].value="${aws configure get aws_session_token}" \
    --timeout 300s \
    --namespace -n value
```

The AWS S3 example assumes files are stored in S3, and are accessible by the iam role used in the example. 
Preconfigured, the example expects data to be stored in the following S3 location: `palisade-application-dev/data/remote-data-store/data/`, with `palisade-application-dev` being the name of the bucket. 
If you receive a `S3 Bucket Doesnt Exist` exception in the `resource-service-0` logs, the bucket name might be incorrect, or the permissions of the user might not be sufficient to read data off the bucket.  
To change the bucket name and default resource information, change the values in the yaml [here](../../example-library/src/main/resources/application-example-s3.yaml), specifically `s3://{bucket-name}/data/remote-data-store`.  
To configure the S3 configuration for the Resource and Data Service, see the yaml files [here](../../../Palisade-readers/s3-resource-service/src/main/resources/application-s3.yaml), and [here](../../../Palisade-readers/s3-data-reader/src/main/resources/application-s3.yaml)

#### Running the example

The example can be run by using either the `runK8sExample.sh` or the `runFormattedK8sExample.sh` scripts. These will run through the scenario presented above, however the "Formatted" script will output
a readable version of the output. These are run from within the `example-runner` pod of the Palisade deployment in AWS.

The following command will 'exec' into the example-runner pod and then run the formatted
script (replace the `"$NAMESPACE"` placeholder for the appropriate namespace value):
```
kubectl exec $(kubectl get pods --namespace="$NAMESPACE" | awk '/example-runner/ {print $1}') --namespace="$NAMESPACE" -- bash -c "cd /usr/share/example-runner && bash ./runFormattedK8sExample.sh"
```

If the formatted example script has been executed then we can verify that the example has returned the expected level of redaction for the employee files using the `verify.sh` script.
This can be run by changing the script name in the command above:
```
kubectl exec $(kubectl get pods --namespace="$NAMESPACE" | awk '/example-runner/ {print $1}') --namespace="$NAMESPACE" -- bash -c "cd /usr/share/example-runner && bash ./verify.sh"
```