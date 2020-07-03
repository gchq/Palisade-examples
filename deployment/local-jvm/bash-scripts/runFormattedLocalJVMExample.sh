#!/usr/bin/env bash

FILE=example-model/target/example-model-*-exec.jar
FORMATTER=deployment/bash-scripts/formatOutput.sh
# Run the formatted rest example
if [ -f $FILE ]; then
  if [ -f $FORMATTER ]; then
    java -Dlogging.level.root=ERROR -Dlogging.level.uk.gov.gchq.palisade.example.model.runner.RestExample=INFO -Dspring.profiles.active=eureka,rest -jar $FILE | $FORMATTER
  else
    echo "Cannot find formatter script -- check your 'git status'"
  fi
else
  echo "Cannot find example-model-<version>-exec.jar - have you run 'mvn install'?"
fi
