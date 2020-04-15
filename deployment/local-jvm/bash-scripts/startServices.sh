#!/usr/bin/env bash

FILE=services-manager/target/services-manager-*-exec.jar
DIR=../Palisade-services/

if [ -d $DIR ]; then
  # Important to cd before running the jar - the working directory must be somewhere under Palisade-services
  cd $DIR
  # Start all the services using the service manager from Palisade-services
  if [ -f $FILE ]; then
    java -jar -Dspring.profiles.active=discovery $FILE --run
    java -jar -Dspring.profiles.active=example,debug $FILE --run
  else
    echo "Cannot find services-manager-<version>-exec.jar - have you run 'mvn install' in Palisade-services?"
  fi
else
  echo "Cannot find Palisade-services directory - have you run 'git clone'?"
fi
