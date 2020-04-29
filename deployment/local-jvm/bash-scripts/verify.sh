#!/usr/bin/env bash

FILE=exampleOutput.txt
DIR=deployment/local-jvm/bash-scripts/

if [ -d $DIR ]; then
  # Important to cd before running the jar - the working directory must be somewhere under Palisade-examples
  cd $DIR
  # Start all the services using the service manager from Palisade-services
  if [ -f $FILE ]; then
    num=$(wc -l $FILE | awk '{ print $1 }')
    echo "$num"
  else
    echo "Cannot find exampleOutput.txt, have you run the example?"
  fi
else
  echo "Cannot find Palisade-examples/deployment/local-jvm/bash-scripts/ directory - have you run 'git clone'?"
fi
