#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar

# Run the bulk resource test
if [ -f $FILE ]; then
  java -jar $FILE --example.directory="$(pwd)/resources/data" --example.type=bulk
else
  echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi