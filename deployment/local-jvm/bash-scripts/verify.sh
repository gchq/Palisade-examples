#!/usr/bin/env bash

num=$(wc -l deployment/local-jvm/bash-scripts/exampleOutput.txt | awk '{ print $1 }')
echo "$num"
