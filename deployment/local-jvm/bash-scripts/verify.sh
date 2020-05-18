#!/usr/bin/env bash

FILE=rest-example.log
DIR=../Palisade-services

if [ -d $DIR ]; then
  # Important to cd before running the jar - the working directory must be deployment/local-jvm/bash-scripts/
  cd $DIR

  if [ -f $FILE ]; then
    num=$(wc -l $FILE | awk '{ print $1 }')

    #check the length of the exampleOutput text file and pass if its 980
    if [ $num == 944 ]; then
      echo "Success - Number of lines was 183"
    else
      echo "Number of lines was not 183 but was: $num"
      exit 1
    fi

  else
    # fail if the example has not been run
    echo "Cannot find rest-example.log, have you started the services?"
    exit 1
  fi
else
  # fail if it cant find the repo
  echo "Cannot find deployment/local-jvm/bash-scripts/ - have you run 'git clone'?"
  exit 1
fi
