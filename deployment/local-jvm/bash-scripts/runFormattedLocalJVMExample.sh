#!/usr/bin/env bash

(java -jar example-model/target/example-model-*-exec.jar --example.filename="$(pwd)/resources/data/employee_file0.avro" --example.type=rest || echo "Error running example-model-<version>-exec.jar - have you run 'mvn install'?") | $EXAMPLE/deployment/bash-scripts/formatOutput.sh
