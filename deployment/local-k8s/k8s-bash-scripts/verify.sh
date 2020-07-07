#!/usr/bin/env bash

FILE=./exampleOutput.txt

if [ -f $FILE ]; then
  num=$(wc -l $FILE | awk '{ print $1 }')
  expected=764

  # check the length of the exampleOutput text file and pass if it's 764
  if [ $num == $expected ]; then
    echo "Success - Number of lines was 764"
  else
    echo "ERROR - Number of lines was not $expected but was: $num"
    echo "*** exampleOutput.txt"
    cat $FILE
    echo "*** pod/audit-service"
    kubectl logs -f $(kubectl get pods | awk '/audit-service/ {print $1}')
    echo "*** pod/data-service"
    kubectl logs -f $(kubectl get pods | awk '/data-service/ {print $1}')
    echo "*** pod/palisade-service"
    kubectl logs -f $(kubectl get pods | awk '/palisade-service/ {print $1}')
    echo "*** pod/policy-service"
    kubectl logs -f $(kubectl get pods | awk '/policy-service/ {print $1}')
    echo "*** pod/resource-service"
    kubectl logs -f $(kubectl get pods | awk '/resource-service/ {print $1}')
    echo "*** pod/user-service"
    kubectl logs -f $(kubectl get pods | awk '/user-service/ {print $1}')
    echo "ERROR - Number of lines was not $expected but was: $num"
    echo "*** exampleOutput.txt"
    exit 1
  fi
else
  # fail if the example has not been run
  echo "Cannot find $FILE, have you run the example?"
  exit 1
fi
