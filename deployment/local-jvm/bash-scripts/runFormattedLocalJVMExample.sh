#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar
FORMATTER=deployment/bash-scripts/formatOutput.sh

# Run the formatted rest example
[ -f $FILE ] && {
  [ -f $FORMATTER ] && {
    java -jar $FILE --example.filename="$(pwd)/resources/data" --example.type=rest | $(pwd)/$FORMATTER
  } || echo "Cannot find formatter script -- check your 'git status'"
} || echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"