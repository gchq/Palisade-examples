#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar

if [[ -f "$FILE" ]]; then
    # Run the example configurator
    java -jar $FILE --example.filename="$(pwd)/resources/data/employee_file0.avro" --example.type=configure
else
    echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
