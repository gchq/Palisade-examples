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

# Check if necessary compiled JAR is present
TARGET_DIR="$(pwd)/hr-data-generator/target"
FILE_PRESENT=0

if [ -d "$TARGET_DIR" ];
then
    JAR_FILE=$(find "$TARGET_DIR" -type f -iname "hr-data-generator-*-jar-with-dependencies.jar")
    if [ ! -z "$JAR_FILE" ];
    then
        FILE_PRESENT=1
    fi
fi

if [ "$FILE_PRESENT" -eq 0 ];then
    echo "Can't find hr-data-generator-<version>.jar in ${TARGET_DIR}. Have you run \"mvn install -P example\" ?"
    exit 1;
fi

# Run the generator
java -cp $JAR_FILE uk.gov.gchq.palisade.example.hrdatagenerator.CreateData $@
