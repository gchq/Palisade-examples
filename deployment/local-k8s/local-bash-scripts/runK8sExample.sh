#! /usr/bin/env bash

kubectl exec -w /usr/share/example-model $(kubectl get pods | awk '/example-model/ {print $1}') -- bash ./runK8sExample.sh
