#!/usr/bin/env bash

kubectl exec $(kubectl get pods | awk '/example-model/ {print $1}') -- bash -c "cd /usr/share/example-model && bash ./verify.sh"
