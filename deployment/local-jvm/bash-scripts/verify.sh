#!/usr/bin/env bash

FILE=exampleOutput.txt
DIR=deployment/local-jvm/bash-scripts/
DIRCHANGE=../../../../Palisade-services

if [ -d $DIR ]; then
  # Important to cd before running the jar - the working directory must be deployment/local-jvm/bash-scripts/
  cd $DIR

  if [ -f $FILE ]; then
    num=$(wc -l $FILE | awk '{ print $1 }')

    #check the length of the exampleOutput text file and pass if it's 781
    if [ $num == 781 ]; then
      echo "Success - Number of lines was 781"
    else
      echo "ERROR - Number of lines was not 781 but was: $num"
          echo "*** exampleOutput.txt"
          cat $FILE
          cd $DIRCHANGE
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
      exit 1
    fi
  else
    # fail if the example has not been run
    echo "Cannot find exampleOutput.txt, have you run the example?"
    exit 1
  fi
else
  # fail if it can't find the repo
  echo "Cannot find deployment/local-jvm/bash-scripts/ - have you run 'git clone'?"
  exit 1
fi
