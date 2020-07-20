#!/bin/bash
# sets up the different paths for calling deployment scripts

export EXAMPLE=$(pwd)
export K8SBASHSCRIPTS="$EXAMPLE/local-k8s/example-model"
export LOCALJVMBASHSCRIPTS="$EXAMPLE/local-jvm/example-model"
export GENERICSCRIPTS="$EXAMPLE/hr-data-generator"
