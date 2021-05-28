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

RUN apk --no-cache add curl=7.77.0-r0
RUN apk --no-cache add bash=5.0.17-r0

RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/${K8_VERSION}/bin/linux/amd64/kubectl && \
      chmod +x ./kubectl && \
      mv ./kubectl /usr/local/bin/kubectl

COPY target/scripts/copyExampleData.sh /bin/copyExampleData.sh

# Force UNIX line-endings
RUN sed -i 's/\r$//' /bin/copyExampleData.sh

# Data-files for configured deployments
COPY target/resources/data /usr/share/deployment/data/example
COPY target/resources/data /usr/share/deployment/data/s3

# Classpath-jars for configured deployments
COPY target/example /usr/share/deployment/classpath-jars/example
COPY target/s3 /usr/share/deployment/classpath-jars/s3

# Make everything readable
RUN chmod ugo+r -R /usr/share/deployment

LABEL service="deployment" project="palisade"
