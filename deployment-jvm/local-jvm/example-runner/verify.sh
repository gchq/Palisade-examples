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

FILE=deployment-jvm/local-jvm/example-runner/exampleOutput.txt
DIR=../Palisade-services

if [ -d $DIR ]; then
  if [ -f $FILE ]; then
    num=$(wc -l $FILE | awk '{ print $1 }')
    expected=509

    # check the length of the exampleOutput text file and pass if it's is the expected number of lines
    if [ $num == $expected ]; then
      echo "Success - Number of lines was $expected"
    else
      echo "*** exampleOutput.txt"
      cat $FILE
      cd $DIR
      echo "*** attribute-masking-service-example.log"
      cat attribute-masking-service-example.log
      echo "*** audit-service-example.log"
      cat audit-service-example.log
      echo "*** data-service-example.log"
      cat data-service-example.log
      echo "*** filtered-resource-service-example.log"
      cat filtered-resource-service-example.log
      echo "*** palisade-service-example.log"
      cat palisade-service-example.log
      echo "*** policy-service-example.log"
      cat policy-service-example.log
      echo "*** resource-service-example.log"
      cat resource-service-example.log
      echo "*** topic-offset-service-example.log"
      cat topic-offset-service-example.log
      echo "*** user-service-example.log"
      cat user-service-example.log
      echo "ERROR - Number of lines was not $expected but was: $num"
      exit 1
    fi
  else
    # fail if the example has not been run
    echo "Cannot find $FILE, have you run the example?"
    exit 1
  fi
else
  # fail if it can't find the repo
  echo "Cannot find $DIR - have you run 'git clone'?"
  exit 1
fi
