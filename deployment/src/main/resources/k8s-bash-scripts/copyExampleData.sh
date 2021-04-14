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

# Current namespace for pods
NAMESPACE=$1
CLASSPATH=$2

# Copy the Dockerfile's ROM-like store of example jars and data compiled into the image during `docker build`
# Once copied to the k8s mountpoints, it will be available to all other services
# This allows upload of data to the cluster independent of storage technology
echo "Copying Dockerfile data to shared cluster storage paths"

# Copy example jars that will be loaded onto the classpath
# These include pre-population configs and example data-types (Employee)
# These are needed by the user, resource, policy and data services
# shellcheck disable=SC2115
rm -rf $CLASSPATH
echo "Removed previous run data for $CLASSPATH"
mkdir -p $CLASSPATH
ls -R $CLASSPATH
cp -vrf /usr/share/example-jars/* $CLASSPATH
echo "Copied example-jars to $CLASSPATH"
ls -R $CLASSPATH

DATASTORE=/data/local-data-store/
# Copy example AVRO data that will be read by the data-service
# The location of these is dependant on the resource-service pre-population values
echo "Preserving previous run data for $DATASTORE"
ls -R $DATASTORE
cp -vrf /usr/share/example-data/resources/data/* $DATASTORE
echo "Copied all example-data to $DATASTORE"
ls -R $DATASTORE

# Restart affected pods and wait until healthy
echo "Restarting pods"
for service in {attribute-masking,audit,data,filtered-resource,palisade,policy,resource,topic-offset,user}-service; do
  eval "kubectl get pods --namespace=\"$NAMESPACE\" | awk '/$service-[-a-z0-9]+/ {print \$1}' | xargs kubectl delete pods";
  echo "Restarted all pods for '$service'"
done
