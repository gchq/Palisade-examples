#!/bin/bash
# Copyright 2020 Crown Copyright
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

# Example usage
# ./copyExampleData.sh default example /mnt/shared-classpath-jars /data/local-data-store

# Stops the execution of a script if a command or pipeline has an error
set -e

# Current namespace for pods
NAMESPACE=$1
# Deployment name used as a selector for which jars/data to copy
DEPLOYMENT=$2
# Target to copy classpath-jars into
CLASSPATH=$3
# Target to copy data-files into
DATASTORE=$4

if [ -z "$NAMESPACE" ] || [ -z "$DEPLOYMENT" ] || [ -z "$CLASSPATH" ] || [ -z "$DATASTORE" ]; then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

# Copy the Dockerfile's ROM-like store of classpath jars and data compiled into the image during `docker build`
# Once copied to the k8s mountpoints, it will be available to all other services
# This allows upload of data to the cluster independent of storage technology
echo "Copying Dockerfile data to shared cluster storage paths"

# Copy classpath jars that will be loaded onto the classpath
# These include pre-population configs and example data-types (Employee)
# These are needed by the user, resource, policy and data services
rm -rfv "$CLASSPATH"
echo "Removed previous run classpath-jars for $CLASSPATH"
mkdir -pv "$CLASSPATH"
ls -R "$CLASSPATH"
cp -vrf /usr/share/deployment/classpath-jars/"$DEPLOYMENT"/* "$CLASSPATH"
echo "Copied classpath-jars to $CLASSPATH"
ls -R "$CLASSPATH"

# Copy example AVRO data that will be read by the data-service
# The location of these is dependant on the resource-service pre-population values
echo "Preserving previous run data-files for $DATASTORE"
ls -R "$DATASTORE"
cp -vrf /usr/share/deployment/data/"$DEPLOYMENT"/* "$DATASTORE"
echo "Copied all data-files to $DATASTORE"
ls -R "$DATASTORE"

# Restart affected pods and wait until healthy
echo "Restarting pods"
for service in {attribute-masking,audit,data,filtered-resource,palisade,policy,resource,topic-offset,user}-service; do
  eval "kubectl get pods --namespace=\"$NAMESPACE\" | awk '/$service-[-a-z0-9]+/ {print \$1}' | xargs kubectl delete pods || true";
  echo "Restarted all pods for '$service'"
done
