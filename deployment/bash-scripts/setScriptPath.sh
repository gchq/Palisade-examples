#!/bin/bash
# sets up the different paths for calling deployment scripts
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
export EXAMPLE=$DIR
# export DOCKERBASHSCRIPTS="$EXAMPLE/deployment/local-docker/bash-scripts"
export K8SBASHSCRIPTS="$EXAMPLE/local-k8s/bash-scripts"
export LOCALJVMBASHSCRIPTS="$EXAMPLE/local-jvm/bash-scripts"
# export EXAMPLESERVICES="$EXAMPLE/example-services"
export GENERICSCRIPTS="$EXAMPLE/bash-scripts"
