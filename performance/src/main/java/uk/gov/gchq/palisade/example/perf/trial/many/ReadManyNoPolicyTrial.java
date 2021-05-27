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
 * Test that reads many data files from Palisade with no policy and times entire Palisade interaction.
 */
@Component
public class ReadManyNoPolicyTrial extends PalisadeTrial {
    protected static final String NAME = "read_many_no_policy";

    /**
     * Default constructor
     */
    public ReadManyNoPolicyTrial() {
        normal = ReadManyNativeTrial.NAME;
    }

    /**
     * Gets the name of the trial
     *
     * @return the name value of the trial
     */
    public String name() {
        return NAME;
    }

    /**
     * Gets the description of the trial
     *
     * @return the description value of the trial
     */
    public String description() {
        return "reads many data files with no policy set";
    }

    /**
     * Run the trial using the provided file sets
     *
     * @param fileSet     a collection of resources in a location that have a number of attached policies
     * @param noPolicySet a collection of resources in a location that do not have any attached policies in palisade
     */
    public void runTrial(final PerfFileSet fileSet, final PerfFileSet noPolicySet) {
        read(noPolicySet.manyDir);
    }
}
