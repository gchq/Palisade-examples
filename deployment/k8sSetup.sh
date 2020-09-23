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

if [ $# -eq 1 ]; then
  read -r NAMESPACE <<< $1

  mkdir -p /usr/share/deployment/classpath/example
  cp -r /usr/share/example-jars /usr/share/deployment/classpath/example
  echo "Copied example-jars to /usr/share/deployment/classpath/example"
  cp -r /usr/share/example-data /data/local-data-store
  echo "Copied example-data to /data/local-data-store"
  kubectl delete pods -n ${NAMESPACE} --all
  echo "Restarted pods for namespace ${NAMESPACE}"
else
  echo "1 argument should be passed"
fi
