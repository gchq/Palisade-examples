#!/usr/bin/env bash

JARFILE=./example-model.jar
OUTPUT=./exampleOutput.txt
# Run the rest example
if [ -f $JARFILE ]; then
  java -Dspring.profiles.active=k8s,rest -jar $JARFILE | tee $OUTPUT
else
  echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
