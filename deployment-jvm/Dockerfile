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

# Copy all the service exec jars, replicating the directory structure of Palisade-services
COPY target/jvm-jars/attribute-masking-service-*.jar /usr/share/deployment-jvm/Palisade-services/attribute-masking-service/target/
COPY target/jvm-jars/audit-service-*.jar /usr/share/deployment-jvm/Palisade-services/audit-service/target/
COPY target/jvm-jars/data-service-*.jar /usr/share/deployment-jvm/Palisade-services/data-service/target/
COPY target/jvm-jars/filtered-resource-service-*.jar /usr/share/deployment-jvm/Palisade-services/filtrered-resource-service/target/
COPY target/jvm-jars/palisade-service-*.jar /usr/share/deployment-jvm/Palisade-services/palisade-service/target/
COPY target/jvm-jars/policy-service-*.jar /usr/share/deployment-jvm/Palisade-services/policy-service/target/
COPY target/jvm-jars/resource-service-*.jar /usr/share/deployment-jvm/Palisade-services/resource-service/target/
COPY target/jvm-jars/topic-offset-service-*.jar /usr/share/deployment-jvm/Palisade-services/topic-offset-service/target/
COPY target/jvm-jars/user-service-*.jar /usr/share/deployment-jvm/Palisade-services/user-service/target/
COPY target/jvm-jars/services-manager-*.jar /usr/share/deployment-jvm/Palisade-services/services-manager/target/

# Copy the example jars, replicating the directory structure of Palisade-examples
COPY target/jvm-jars/example-runner-*.jar /usr/share/deployment-jvm/Palisade-examples/example-runner/target/
COPY target/jvm-jars/performance-*.jar /usr/share/deployment-jvm/Palisade-examples/performance/target/
COPY target/jvm-jars/example-library-*.jar /usr/share/deployment-jvm/Palisade-examples/example-library/target/

# Copy the resources and scripts
COPY target/resources /usr/share/deployment-jvm/Palisade-examples/resources/
COPY target/scripts /usr/share/deployment-jvm/Palisade-examples/deployment-jvm/local-jvm/
RUN sed -i 's/\r$//' /usr/share/deployment-jvm/Palisade-examples/deployment-jvm/local-jvm/*/*.sh

LABEL service="deployment-jvm" project="palisade"
