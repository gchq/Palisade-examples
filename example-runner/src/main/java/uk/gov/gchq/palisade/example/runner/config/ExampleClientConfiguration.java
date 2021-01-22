/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.palisade.example.runner.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.clients.simpleclient.web.DataClientFactory;
import uk.gov.gchq.palisade.clients.simpleclient.web.PalisadeClient;
import uk.gov.gchq.palisade.example.runner.client.ExampleSimpleClient;

@Configuration
@EnableFeignClients("uk.gov.gchq.palisade.clients.simpleclient")
public class ExampleClientConfiguration {
    @Bean("ExampleClient")
    public ExampleSimpleClient exampleClient(final PalisadeClient palisadeClient, final DataClientFactory dataClient) {
        return new ExampleSimpleClient(palisadeClient, dataClient);
    }
}
