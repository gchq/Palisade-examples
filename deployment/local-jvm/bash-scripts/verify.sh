#!/usr/bin/env bash

FILE=rest-example.log
DIR=../Palisade-services/

if [ -d $DIR ]; then
  # Important to cd before running the jar - the working directory must be somewhere under Palisade-services
  cd $DIR
  # Start all the services using the service manager from Palisade-services
  if [ -f $FILE ]; then
    num=$(wc -l $FILE | awk '{ print $1 }')
    echo "$num"
  else
    echo "Cannot find rest-example.log, have you run the example?"
  fi
else
  echo "Cannot find Palisade-services directory - have you run 'git clone'?"
fi
