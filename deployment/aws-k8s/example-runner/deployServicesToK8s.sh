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
   echo -e "\t-n(amespace)     The name for the K8s namespace to deploy to"
   echo -e "\t-r(epository)    The URL for the (ECR) repository"
   echo -e "\t-h(ostname)      The URL for the (ELB) hostname of the cluster deployment"
   echo -e "\t-d(atastore)     The URL for the (EFS) aws volume handle used as a data-store"
   echo -e "\t-c(lasspathjars) The URL for the (EFS) aws volume handle used for storing classpath JARs"
   echo -e "\t-t(raefik)       The boolean true/false value for installing traefik"
   echo -e "\t-P(refix)        The topic prefix so we generate unique topic names"
   exit 1 # Exit script after printing help
}

while getopts "n:r:h:d:c:t:P:" opt
do
   case "$opt" in
      n) namespace="$OPTARG" ;;
      r) repository="$OPTARG" ;;
      h) hostname="$OPTARG" ;;
      d) datastore="$OPTARG" ;;
      c) classpathjars="$OPTARG" ;;
      t) traefik="$OPTARG" ;;
      P) topicprefix="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Print helpFunction in case parameters are empty
if [ -z "$namespace" ] || [ -z "$repository" ] || [ -z "$hostname" ] || [ -z "$datastore" ] || [ -z "$classpathjars" ] || [ -z "$traefik" ]; then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

# Begin script in case all parameters are correct
helm dep up

# Added extra params to ensure that AWS deploys use the shared one at 'palisade-shared' DNS
helm upgrade --install --wait palisade . \
    --set global.hosting=aws \
    --set global.repository="${repository}" \
    --set global.hostname="${hostname}" \
    --set global.persistence.dataStores.palisade-data-store.aws.volumeHandle="${datastore}" \
    --set global.persistence.classpathJars.aws.volumeHandle="${classpathjars}" \
    --set global.deployment=example \
    --set global.kafka.install=false \
    --set global.redis.install=false \
    --set traefik.install="${traefik}" \
    --set global.topicPrefix="${topicprefix}" \
    --timeout 300s \
    --namespace "${namespace}"
