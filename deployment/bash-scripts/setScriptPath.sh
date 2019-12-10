#!/bin/bash
# sets up the different paths for calling deployment scripts
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
EXAMPLE=$(pwd)

export EXAMPLE="$EXAMPLE"
export K8SBASHSCRIPTS="$EXAMPLE/local-k8s/bash-scripts"
export LOCALJVMBASHSCRIPTS="$EXAMPLE/local-jvm/bash-scripts"
export GENERICSCRIPTS="$EXAMPLE/bash-scripts"
