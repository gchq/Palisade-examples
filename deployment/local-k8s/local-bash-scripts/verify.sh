#!/usr/bin/env bash

kubectl exec $(kubectl get pods | awk '/example-model/ {print $1}') -- /usr/share/example-model/verify.sh
