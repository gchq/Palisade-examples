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

package uk.gov.gchq.palisade.example.perf.config;

import uk.gov.gchq.palisade.example.perf.common.Generated;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class PerformanceConfiguration {
    private String action = "run";
    private String directory;
    private int small;
    private int large;
    private int manyUnique;
    private int manyDuplicates;
    private int dryRuns;
    private int liveRuns;
    private List<String> skipTests;
    private String userId;
    private String purpose;

    @Generated
    public String getAction() {
        return action;
    }

    @Generated
    public void setAction(final String action) {
        requireNonNull(action);
        this.action = action;
    }

    @Generated
    public String getDirectory() {
        return directory;
    }

    @Generated
    public void setDirectory(final String directory) {
        requireNonNull(directory);
        this.directory = directory;
    }

    @Generated
    public int getSmall() {
        return small;
    }

    @Generated
    public void setSmall(final int small) {
        this.small = small;
    }

    @Generated
    public int getLarge() {
        return large;
    }

    @Generated
    public void setLarge(final int large) {
        this.large = large;
    }

    @Generated
    public int getManyUnique() {
        return manyUnique;
    }

    @Generated
    public void setManyUnique(final int manyUnique) {
        this.manyUnique = manyUnique;
    }

    @Generated
    public int getManyDuplicates() {
        return manyDuplicates;
    }

    @Generated
    public void setManyDuplicates(final int manyDuplicates) {
        this.manyDuplicates = manyDuplicates;
    }

    @Generated
    public int getDryRuns() {
        return dryRuns;
    }

    @Generated
    public void setDryRuns(final int dryRuns) {
        this.dryRuns = dryRuns;
    }

    @Generated
    public int getLiveRuns() {
        return liveRuns;
    }

    @Generated
    public void setLiveRuns(final int liveRuns) {
        this.liveRuns = liveRuns;
    }

    @Generated
    public List<String> getSkipTests() {
        return new ArrayList<>(skipTests);
    }

    @Generated
    public void setSkipTests(final List<String> skipTests) {
        requireNonNull(skipTests);
        this.skipTests = new ArrayList<>(skipTests);
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
