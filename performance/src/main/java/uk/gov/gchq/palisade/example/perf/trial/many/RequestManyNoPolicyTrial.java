/*
 * Copyright 2018-2021 Crown Copyright
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

package uk.gov.gchq.palisade.example.perf.trial.many;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.PalisadeTrial;

/**
 * Sets up a data request through Palisade, but doesn't read any data back.
 */
@Component
public class RequestManyNoPolicyTrial extends PalisadeTrial {
    protected static final String NAME = "request_many_no_policy";

    public RequestManyNoPolicyTrial() {
        normal = NAME;
    }

    public String name() {
        return NAME;
    }

    public String description() {
        return "makes a request for many files with no policy set without reading data";
    }

    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        query(noPolicySet.manyDir);
    }
}
