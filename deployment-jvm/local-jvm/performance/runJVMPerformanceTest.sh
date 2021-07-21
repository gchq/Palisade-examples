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

JAR_FILE=performance/target/performance-*-exec.jar
# Run the JVM performance test
if [ -f $JAR_FILE ]; then
  java -jar -Dspring.profiles.active=static $JAR_FILE --performance.action=run
else
  echo "Cannot find performance-<version>-exec.jar - have you run 'mvn install'?"
fi
