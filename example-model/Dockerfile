# Copyright 2020 Crown Copyright
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

FROM openjdk:11@sha256:736dd07fc5cb53060a49d38c86b9e673974510c5a18afc40b1c936475f59aee7
VOLUME /tmp

ARG JAR_FILE
ARG K8_VERSION=v1.15.3

RUN apt update && \
      apt install -y curl && \
      curl -LO https://storage.googleapis.com/kubernetes-release/release/${K8_VERSION}/bin/linux/amd64/kubectl && \
      chmod +x ./kubectl && \
      mv ./kubectl /usr/local/bin/kubectl

COPY target/container-scripts /usr/share/example-model
COPY $JAR_FILE /usr/share/example-model/example-model.jar

# Needed for converting all bash scripts windows line endings into unix
RUN sed -i 's/\r$//' /usr/share/example-model/*.sh

LABEL service="example" project="palisade"
