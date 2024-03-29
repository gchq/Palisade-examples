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

FROM alpine:3.12.0@sha256:90baa0922fe90624b05cb5766fa5da4e337921656c2f8e2b13bd3c052a0baac1
VOLUME /tmp

ARG K8_VERSION=v1.15.3

RUN apk add --no-cache curl~=7
RUN apk add --no-cache bash~=5

RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/${K8_VERSION}/bin/linux/amd64/kubectl && \
      chmod +x ./kubectl && \
      mv ./kubectl /usr/local/bin/kubectl

COPY target/scripts/copyExampleData.sh /bin/copyExampleData.sh

# Data-files for configured deployments
COPY target/resources/data /usr/share/deployment-k8s/data/example
COPY target/resources/data /usr/share/deployment-k8s/data/performance-test
COPY target/resources/data /usr/share/deployment-k8s/data/example-s3

# Classpath-jars for configured deployments
COPY target/example /usr/share/deployment-k8s/classpath-jars/example
COPY target/example /usr/share/deployment-k8s/classpath-jars/performance-test
COPY target/example-s3 /usr/share/deployment-k8s/classpath-jars/example-s3

# Make everything readable
RUN chmod ugo+r -R /usr/share/deployment-k8s

LABEL service="deployment-k8s" project="palisade"
