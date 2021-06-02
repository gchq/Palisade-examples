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

ARG BASE_IMG
FROM $BASE_IMG

VOLUME /tmp

ARG CURL_VERSION=7.64.0-4+deb10u2

RUN apt-get update -y && apt-get install -y curl=${CURL_VERSION} --no-install-recommends \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

ARG K8_VERSION=v1.15.3
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/${K8_VERSION}/bin/linux/amd64/kubectl && \
      chmod +x ./kubectl && \
      mv ./kubectl /usr/local/bin/kubectl

ARG JAR_FILE
COPY target/classes/k8s-bash-scripts /usr/share/example-runner
COPY $JAR_FILE /usr/share/example-runner/example-runner.jar

# Needed for converting all bash scripts windows line endings into unix
RUN sed -i 's/\r$//' /usr/share/example-runner/*.sh

LABEL service="example-runner" project="palisade"
