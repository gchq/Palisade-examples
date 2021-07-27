<!--
 Copyright 2018-2021 Crown Copyright
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# <img src="../logos/logo.svg" width="180">

## A Tool for Complex and Scalable Data Access Policy Enforcement

Contains the deployment specific code and scripts for running the example within a Kubernetes environment.  

The Dockerfile is used to create a deployment image that will hold the data and supporting resources needed for testing in each of the different environments.

# Deployment

A collection of scripts to allow the example to be run in different types of Kubernetes environment.

## [aws-K8s](./aws-k8s/README.md)
Scripts for running the examples in an AWS K8s cluster

## [aws-s3-K8s](./aws-s3-k8s/README.md)
Scripts for running the examples in an AWS K8s cluster where the data is stored in a S3 bucket

## [local-K8s](./local-k8s/README.md)
Scripts for running the examples in a local K8s cluster
This includes both the local example that is provided in the AWS version of the test as well as a performance test for the local K8s environment

