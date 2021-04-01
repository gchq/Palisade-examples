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

package uk.gov.gchq.palisade.example.perf.analysis;

import uk.gov.gchq.palisade.client.akka.common.Generated;

/**
 * Utility class.
 */
public class PerfStats {
    private double numTrials;
    private double min;
    private double max;
    private double mean;
    private double stdDev;
    private double pc25;
    private double pc50;
    private double pc75;
    private double pc99;
    private double norm;

    @Generated
    public double getNumTrials() {
        return numTrials;
    }

    @Generated
    public void setNumTrials(final double numTrials) {
        this.numTrials = numTrials;
    }

    @Generated
    public double getMin() {
        return min;
    }

    @Generated
    public void setMin(final double min) {
        this.min = min;
    }

    @Generated
    public double getMax() {
        return max;
    }

    @Generated
    public void setMax(final double max) {
        this.max = max;
    }

    @Generated
    public double getMean() {
        return mean;
    }

    @Generated
    public void setMean(final double mean) {
        this.mean = mean;
    }

    @Generated
    public double getStdDev() {
        return stdDev;
    }

    @Generated
    public void setStdDev(final double stdDev) {
        this.stdDev = stdDev;
    }

    @Generated
    public double getPc25() {
        return pc25;
    }

    @Generated
    public void setPc25(final double pc25) {
        this.pc25 = pc25;
    }

    @Generated
    public double getPc50() {
        return pc50;
    }

    @Generated
    public void setPc50(final double pc50) {
        this.pc50 = pc50;
    }

    @Generated
    public double getPc75() {
        return pc75;
    }

    @Generated
    public void setPc75(final double pc75) {
        this.pc75 = pc75;
    }

    @Generated
    public double getPc99() {
        return pc99;
    }

    @Generated
    public void setPc99(final double pc99) {
        this.pc99 = pc99;
    }

    @Generated
    public double getNorm() {
        return norm;
    }

    @Generated
    public void setNorm(final double norm) {
        this.norm = norm;
    }
}
