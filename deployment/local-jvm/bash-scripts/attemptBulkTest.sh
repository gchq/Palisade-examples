#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar

if [[ -f "$FILE" ]]; then
    # Run the bulk resource test
    java -jar $FILE --example.directory="$(pwd)/resources/data" --example.type=bulk
else
    echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
