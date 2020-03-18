#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar || echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
FORMATTER=deployment/bash-scripts/formatOutput.sh || echo "Cannot find formatter script -- check your 'git status'"

# Run the formatted rest example
java -jar $FILE --example.filename="$(pwd)/resources/data" --example.type=rest | $(pwd)/$FORMATTER
