#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar
FORMATTER=deployment/bash-scripts/formatOutput.sh

if [[ -f "$FILE" ]]; then
    # Run the formatted rest example
    java -jar $FILE --example.filename="$(pwd)/resources/data" --example.type=rest | $(pwd)/$FORMATTER
else
    echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
