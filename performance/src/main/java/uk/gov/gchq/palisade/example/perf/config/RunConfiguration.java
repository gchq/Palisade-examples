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

package uk.gov.gchq.palisade.example.perf.config;

import uk.gov.gchq.palisade.Generated;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class RunConfiguration {
    private String filename;
    private int dryRuns;
    private int liveRuns;
    private List<String> skipTests;
    private String userId;
    private String purpose;

    @Generated
    public String getFilename() {
        return filename;
    }

    @Generated
    public void setFilename(final String filename) {
        requireNonNull(filename);
        this.filename = filename;
    }

    @Generated
    public int getDryRuns() {
        return dryRuns;
    }

    @Generated
    public void setDryRuns(final int dryRuns) {
        requireNonNull(dryRuns);
        this.dryRuns = dryRuns;
    }

    @Generated
    public int getLiveRuns() {
        return liveRuns;
    }

    @Generated
    public void setLiveRuns(final int liveRuns) {
        requireNonNull(liveRuns);
        this.liveRuns = liveRuns;
    }

    @Generated
    public List<String> getSkipTests() {
        return skipTests;
    }

    @Generated
    public void setSkipTests(final List<String> skipTests) {
        requireNonNull(skipTests);
        this.skipTests = skipTests;
    }

    @Generated
    public String getUserId() {
        return userId;
    }

    @Generated
    public void setUserId(final String userId) {
        requireNonNull(userId);
        this.userId = userId;
    }

    @Generated
    public String getPurpose() {
        return purpose;
    }

    @Generated
    public void setPurpose(final String purpose) {
        requireNonNull(purpose);
        this.purpose = purpose;
    }
}
