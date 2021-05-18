#!/usr/bin/env bash
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

NAMESPACE=$1

if [ -z "$NAMESPACE" ];
then
  # If the user doesnt pass in a namespace
  kubectl exec $(kubectl get pods | awk '/deployment-jvm/ {print $1}') -- bash -c \
  "cd /usr/share/deployment-jvm/Palisade-examples/ && " \
  "./deployment-jvm/local-jvm/example-runner/startServices.sh && " \
  "./deployment-jvm/local-jvm/example-runner/runFormattedLocalJVMExample.sh.sh && " \
  "./deployment-jvm/local-jvm/example-runner/verify.sh && " \
  "./deployment-jvm/local-jvm/example-runner/stopServices.sh"
else
  # If the user passes in a namespace, use the namespace in the kubectl command
  kubectl exec $(kubectl get pods -n "$NAMESPACE" | awk '/deployment-jvm/ {print $1}') -n "$NAMESPACE" -- bash -c \
  "cd /usr/share/deployment-jvm/Palisade-examples/ && " \
  "./deployment-jvm/local-jvm/example-runner/startServices.sh && " \
  "./deployment-jvm/local-jvm/example-runner/runFormattedLocalJVMExample.sh.sh && " \
  "./deployment-jvm/local-jvm/example-runner/verify.sh && " \
  "./deployment-jvm/local-jvm/example-runner/stopServices.sh"
fi
