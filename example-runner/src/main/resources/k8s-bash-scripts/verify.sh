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
#

FILE=./exampleOutput.txt

if [ -f $FILE ]; then
  num=$(wc -l $FILE | awk '{ print $1 }')
  expected=509

  # check the length of the exampleOutput text file and pass if it's the expected number of lines
  if [ $num == $expected ]; then
    echo "Success - Number of lines was $expected"
  else
    echo "*** exampleOutput.txt"
    cat $FILE
    echo "*** statefulSet/attribute-masking-service"
    kubectl logs statefulset/attribute-masking-service
    echo "*** statefulSet/audit-service"
    kubectl logs statefulset/audit-service
    echo "*** statefulSet/data-service"
    kubectl logs statefulset/data-service
    echo "*** statefulSet/filtered-resource-service"
    kubectl logs statefulset/filtered-resource-service
    echo "*** statefulSet/palisade-service"
    kubectl logs statefulset/palisade-service
    echo "*** statefulSet/policy-service"
    kubectl logs statefulset/policy-service
    echo "*** statefulSet/resource-service"
    kubectl logs statefulset/resource-service
    echo "*** statefulSet/topic-offset-service"
    kubectl logs statefulset/topic-offset-service
    echo "*** statefulSet/user-service"
    kubectl logs statefulset/user-service
    echo "ERROR - Number of lines was not $expected but was: $num"
    exit 1
  fi
else
  # fail if the example has not been run
  echo "Cannot find $FILE, have you run the example?"
  exit 1
fi
