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

package uk.gov.gchq.palisade.example.perf.trial.large;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.AbstractPalisadeTrial;

/**
 * Test that reads the large data file from Palisade with an example policy and times entire Palisade interaction.
 */
@Component
public class ReadLargeWithPolicyTrial extends AbstractPalisadeTrial {
    protected static final String NAME = "read_large_with_policy";
    private static final String DESCRIPTION = "reads the large data file with an example policy set";

    /**
     * Default constructor
     */
    public ReadLargeWithPolicyTrial() {
        normal = ReadLargeNativeTrial.NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String name() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String description() {
        return DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        read(fileSet.largeFile);
    }
}
