#! /usr/bin/env bash

kubectl exec $(kubectl get pods | awk '/example-model/ {print $1}') -- bash -c "cd /usr/share/example-model && ./runFormattedK8sExample.sh"
