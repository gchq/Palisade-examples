#!/usr/bin/env bash

JARFILE=./example-model.jar
OUTPUT=./exampleOutput.txt
FORMATTER=./formatOutput.sh
# Run the formatted rest example
if [ -f $JARFILE ]; then
  if [ -f $FORMATTER ]; then
    java -Dlogging.level.root=ERROR -Dlogging.level.uk.gov.gchq.palisade.example.model.runner.RestExample=INFO -Dspring.profiles.active=k8s,rest -jar $JARFILE | $FORMATTER | tee $OUTPUT
  else
    echo "Cannot find formatter script -- check your 'git status'"
  fi
else
  echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
