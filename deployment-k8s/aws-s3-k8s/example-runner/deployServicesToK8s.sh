#! /usr/bin/env bash
# Copyright 2018-2021 Crown Copyright
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

helpFunction() {
   echo ""
   echo "Usage: $(basename "$0") [OPTIONS]"
   echo -e "\t-n(amespace)                The name for the K8s namespace to deploy to"
   echo -e "\t-r(epository)               The URL for the (ECR) repository"
   echo -e "\t-h(ostname)                 The URL for the (ELB) hostname of the cluster deployment"
   echo -e "\t-d(atastore)                The URL for the (EFS) aws volume handle used as a data-store"
   echo -e "\t-c(lasspathjars)            The URL for the (EFS) aws volume handle used for storing classpath JARs"
   echo -e "\t-t(raefik)                  The boolean true/false value for installing traefik"
   echo -e "\t-P(refix)                   The topic prefix so we generate unique topic names"
   echo -e "\t-R(ole)                     Aws iam role name"
   exit 1 # Exit script after printing help
}

while getopts "n:r:h:d:c:t:P:R:" opt
do
   case "$opt" in
      n) namespace="$OPTARG" ;;
      r) repository="$OPTARG" ;;
      h) hostname="$OPTARG" ;;
      d) datastore="$OPTARG" ;;
      c) classpathjars="$OPTARG" ;;
      t) traefik="$OPTARG" ;;
      P) topicprefix="$OPTARG" ;;
      R) role="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Print helpFunction in case parameters are empty
if [ -z "$namespace" ] || [ -z "$repository" ] || [ -z "$hostname" ] || [ -z "$datastore" ] || [ -z "$classpathjars" ] || [ -z "$traefik" ] || [ -z "$role" ]; then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

# get iam-role from command-line args, groovy-scripts does ./deployServicesToK8s.sh -r palisade-pipeline-prod-worker
# get security credentials from instance metadata
if curl --fail --connect-timeout 5 -o security-credentials.json http://169.254.169.254/latest/meta-data/iam/security-credentials/${role}/; then
    AWS_ACCESS_KEY_ID=$(jq -r '.AccessKeyId' < security-credentials.json)
    AWS_SECRET_ACCESS_KEY=$(jq -r '.SecretAccessKey' < security-credentials.json)
    AWS_SESSION_TOKEN=$(jq -r '.Token' < security-credentials.json)
else
    echo "failed to get sec credentials"
    exit 1
fi

cd deployment-k8s || exit

# Begin script in case all parameters are correct
helm dep up

echo helm upgrade --install --wait palisade . \
    --set global.hosting=aws \
    --set global.repository="${repository}/" \
    --set global.hostname="${hostname}" \
    --set global.persistence.dataStores.palisade-data-store.aws.volumeHandle="${datastore}" \
    --set global.persistence.classpathJars.aws.volumeHandle="${classpathjars}" \
    --set global.deployment=example-s3 \
    --set global.kafka.install=true \
    --set global.redis.install=true \
    --set Palisade-services.traefik.install="${traefik}" \
    --set global.topicPrefix="${topicprefix}" \
    --set global.env.example-s3[0].name="SPRING_PROFILES_ACTIVE" \
    --set global.env.example-s3[0].value="k8s\,\s3\,\example-s3\,\debug" \
    --set global.env.example-s3[1].name="AWS_ACCESS_KEY_ID" \
    --set global.env.example-s3[1].value="${AWS_ACCESS_KEY_ID}" \
    --set global.env.example-s3[2].name="AWS_SECRET_ACCESS_KEY" \
    --set global.env.example-s3[2].value="${AWS_SECRET_ACCESS_KEY}" \
    --set global.env.example-s3[3].name="AWS_SESSION_TOKEN" \
    --set global.env.example-s3[3].value="${AWS_SESSION_TOKEN}" \
    --timeout 300s \
    --namespace "${namespace}"

helm upgrade --install --wait palisade . \
    --set global.hosting=aws \
    --set global.repository="${repository}/" \
    --set global.hostname="${hostname}" \
    --set global.persistence.dataStores.palisade-data-store.aws.volumeHandle="${datastore}" \
    --set global.persistence.classpathJars.aws.volumeHandle="${classpathjars}" \
    --set global.deployment=example-s3 \
    --set global.kafka.install=true \
    --set global.redis.install=true \
    --set Palisade-services.traefik.install="${traefik}" \
    --set global.topicPrefix="${topicprefix}" \
    --set global.env.example-s3[0].name="SPRING_PROFILES_ACTIVE" \
    --set global.env.example-s3[0].value="k8s\,\s3\,\example-s3\,\debug" \
    --set global.env.example-s3[1].name="AWS_ACCESS_KEY_ID" \
    --set global.env.example-s3[1].value="${AWS_ACCESS_KEY_ID}" \
    --set global.env.example-s3[2].name="AWS_SECRET_ACCESS_KEY" \
    --set global.env.example-s3[2].value="${AWS_SECRET_ACCESS_KEY}" \
    --set global.env.example-s3[3].name="AWS_SESSION_TOKEN" \
    --set global.env.example-s3[3].value="${AWS_SESSION_TOKEN}" \
    --timeout 300s \
    --namespace "${namespace}"
