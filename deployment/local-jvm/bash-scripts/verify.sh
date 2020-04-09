#!/usr/bin/env bash

num=$(wc -l exampleOutput.txt | awk '{ print $1 }')
echo "$num"
