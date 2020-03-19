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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import uk.gov.gchq.palisade.example.request.AddSerialiserRequest;
import uk.gov.gchq.palisade.example.request.ReadRequest;

import java.net.URI;

@FeignClient(name = "data-service", url = "${web.client.data-service}")
public interface DataClient {

    @PostMapping(value = "/read/chunked", consumes = "application/json", produces = "application/octet-stream")
    ResponseEntity<StreamingResponseBody> readChunked(final URI url, @RequestBody final ReadRequest request);

    @PostMapping(value = "/addSerialiser", consumes = "application/json", produces = "application/json")
    Boolean addSerialiser(final URI url, @RequestBody final AddSerialiserRequest request);

}
