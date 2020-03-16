#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar

if [[ -f "$FILE" ]]; then
    # Run the rest example
    java -jar $FILE --example.filename="$(pwd)/resources/data" --example.type=rest
else
    echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
