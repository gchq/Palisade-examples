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

val=$(kubectl get ns $1)

if [ -z "$val" ]; then
  NAMESPACE=$1
  printenv

  mkdir -p /usr/share/deployment/classpath/example
  cp -r /usr/share/example-jars /usr/share/deployment/classpath/example
  echo "Copied example-jars to /usr/share/deployment/classpath/example"
  cp -r /usr/share/example-data/resources/data/employee_file0.avro /data/local-data-store
  cp -r /usr/share/example-data/resources/data/employee_file1.avro /data/local-data-store
  echo "Copied example-data to /data/local-data-store"

#  for pod in "${!PODS@}"; do
#    echo "Attempting to restart the ${pod}"
#    service=$(kubectl get pods --namespace="$NAMESPACE" | awk '"$pod" {print $1}')
#    kubectl delete pod -n ${NAMESPACE} ${service}
#  done

else
  echo "The Kubernetes namespace value should be passed as an argument"
fi
