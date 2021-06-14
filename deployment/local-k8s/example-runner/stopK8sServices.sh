#! /usr/bin/env bash
# Copyright 2018-2021 Crown Copyright
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

NAMESPACE=$1

cd deployment

if [ -z "$NAMESPACE" ]
then
    echo "Delete all the resources in the default namespace"
    # Delete the existing helm deployment
    helm delete palisade

    # Delete all the resources
    kubectl delete jobs --all
    kubectl delete pods --all
    kubectl delete pvc --all
    kubectl delete pv --all
else
    echo "Delete all the resources in the $NAMESPACE namespace"
    # Delete the existing helm deployment
    helm delete palisade

    # Delete all the resources in the namespace
    kubectl delete namespace $NAMESPACE
    kubectl delete pv -n $NAMESPACE --all

fi
