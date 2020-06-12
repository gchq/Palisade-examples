#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar
# Run the rest example
if [ -f $FILE ]; then
  java -Dspring.profiles.active=eureka,rest -jar $FILE
else
  echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
