#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar

# Run the example configurator
if [ -f $FILE ]; then
  java -jar $FILE --example.filename="$(pwd)/resources/data" --example.type=configure
else
  echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi