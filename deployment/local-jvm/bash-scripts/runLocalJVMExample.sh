#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar

# Run the rest example
[ -f $FILE ] && {
  java -jar $FILE --example.filename="$(pwd)/resources/data" --example.type=rest
} || echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"