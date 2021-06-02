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

RUN apk --no-cache add curl=~7.77.0-r0
RUN apk --no-cache add bash=~5.0.17-r0

RUN apk info curl
RUN apk info bash

RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/${K8_VERSION}/bin/linux/amd64/kubectl && \
      chmod +x ./kubectl && \
      mv ./kubectl /usr/local/bin/kubectl

COPY target/example /usr/share/example-jars/
COPY target/resources /usr/share/example-data/resources/
COPY target/scripts/copyExampleData.sh /bin/copyExampleData.sh
RUN sed -i 's/\r$//' /bin/copyExampleData.sh

LABEL service="deployment" project="palisade"
