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

package uk.gov.gchq.palisade.example.perf.trial.large;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.client.SimpleClient;
import uk.gov.gchq.palisade.example.perf.trial.PalisadeTrial;

/**
 * Test that reads the large data file from Palisade with no policy and times entire Palisade interaction.
 */
@Component
public class ReadLargeNoPolicyTrial extends PalisadeTrial {
    protected static final String NAME = "read_large_no_policy";

    public ReadLargeNoPolicyTrial(final SimpleClient client) {
        super(client);
        normal = ReadLargeNativeTrial.NAME;
    }

    public String name() {
        return NAME;
    }

    public String description() {
        return "reads the large data file with no policy set";
    }

    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        read(noPolicySet.largeFile);
    }
}
