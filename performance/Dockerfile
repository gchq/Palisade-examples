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

ARG BASE_IMG
FROM $BASE_IMG

VOLUME /tmp

ARG JAR_FILE

COPY target/classes/k8s-bash-scripts /usr/share/performance
COPY $JAR_FILE /usr/share/performance/performance.jar

# Needed for converting all bash scripts windows line endings into unix
RUN sed -i 's/\r$//' /usr/share/performance/*.sh

LABEL service="performance" project="palisade"
