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

package uk.gov.gchq.palisade.example.perf.trial.small;

import org.springframework.stereotype.Component;

import uk.gov.gchq.palisade.example.perf.analysis.PerfFileSet;
import uk.gov.gchq.palisade.example.perf.trial.AbstractPalisadeTrial;

/**
 * Test that reads the small data file from Palisade with no policy and times entire Palisade interaction.
 */
@Component
public class ReadSmallNoPolicyTrial extends AbstractPalisadeTrial {
    protected static final String NAME = "read_small_no_policy";
    private static final String DESCRIPTION = "reads the small data file with no policy set";

    /**
     * Default constructor
     */
    public ReadSmallNoPolicyTrial() {
        normal = ReadSmallNativeTrial.NAME;
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
        read(noPolicySet.smallFile);
    }
}
