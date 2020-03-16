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
package uk.gov.gchq.palisade.example.web;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import uk.gov.gchq.palisade.example.request.SetResourcePolicyRequest;

import java.net.URI;

@FeignClient(name = "policy-service", url = "${web.client.policy-service}")
public interface PolicyClient {

    @PutMapping(path = "/setResourcePolicyAsync", consumes = "application/json", produces = "application/json")
    void setResourcePolicyAsync(final URI url, @RequestBody final SetResourcePolicyRequest request);

}
