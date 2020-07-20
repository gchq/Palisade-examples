#!/usr/bin/env bash
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
#

FILE=./exampleOutput.txt

if [ -f $FILE ]; then
  num=$(wc -l $FILE | awk '{ print $1 }')
  expected=764

  # check the length of the exampleOutput text file and pass if it's the expected number of lines
  if [ $num == $expected ]; then
    echo "Success - Number of lines was $expected"
  else
    echo "*** exampleOutput.txt"
    cat $FILE
    echo "*** pod/audit-service"
    kubectl logs $(kubectl get pods | awk '/audit-service/ {print $1}')
    echo "*** pod/data-service"
    kubectl logs $(kubectl get pods | awk '/data-service/ {print $1}')
    echo "*** pod/palisade-service"
    kubectl logs $(kubectl get pods | awk '/palisade-service/ {print $1}')
    echo "*** pod/policy-service"
    kubectl logs $(kubectl get pods | awk '/policy-service/ {print $1}')
    echo "*** pod/resource-service"
    kubectl logs $(kubectl get pods | awk '/resource-service/ {print $1}')
    echo "*** pod/user-service"
    kubectl logs $(kubectl get pods | awk '/user-service/ {print $1}')
    echo "ERROR - Number of lines was not $expected but was: $num"
    exit 1
  fi
else
  # fail if the example has not been run
  echo "Cannot find $FILE, have you run the example?"
  exit 1
fi
