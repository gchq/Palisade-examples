#!/usr/bin/env bash

FILE=deployment/local-jvm/bash-scripts/exampleOutput.txt
DIR=../Palisade-services

if [ -d $DIR ]; then
  if [ -f $FILE ]; then
    num=$(wc -l $FILE | awk '{ print $1 }')
    expected=764

    # check the length of the exampleOutput text file and pass if it's 764
    if [ $num == $expected ]; then
      echo "Success - Number of lines was 764"
    else
      echo "*** exampleOutput.txt"
      cat $FILE
      cd $DIR
      echo "*** audit-service-example.log"
      cat audit-service-example.log
      echo "*** data-service-example.log"
      cat data-service-example.log
      echo "*** discovery-service.log"
      cat discovery-service.log
      echo "*** palisade-service-example.log"
      cat palisade-service-example.log
      echo "*** policy-service-example.log"
      cat policy-service-example.log
      echo "*** resource-service-example.log"
      cat resource-service-example.log
      echo "*** user-service-example.log"
      cat user-service-example.log
      echo "ERROR - Number of lines was not $expected but was: $num"
      exit 1
    fi
  else
    # fail if the example has not been run
    echo "Cannot find $FILE, have you run the example?"
    exit 1
  fi
else
  # fail if it can't find the repo
  echo "Cannot find $DIR - have you run 'git clone'?"
  exit 1
fi
