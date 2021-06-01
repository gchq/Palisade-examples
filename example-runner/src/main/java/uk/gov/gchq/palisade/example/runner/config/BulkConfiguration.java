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

package uk.gov.gchq.palisade.example.runner.config;

import uk.gov.gchq.palisade.Generated;

import java.util.Optional;

/**
 * Configuration for creating resources in bulk
 */
public class BulkConfiguration {
    private String directory;
    private Integer quantity;
    private Integer copies;
    private boolean shouldCreate;
    private boolean shouldDelete;

    @Generated
    public String getDirectory() {
        return directory;
    }

    @Generated
    public void setDirectory(final String directory) {
        this.directory = Optional.ofNullable(directory)
                .orElseThrow(() -> new IllegalArgumentException("directory cannot be null"));
    }

    @Generated
    public Integer getQuantity() {
        return quantity;
    }

    @Generated
    public void setQuantity(final Integer quantity) {
        this.quantity = Optional.ofNullable(quantity)
                .orElseThrow(() -> new IllegalArgumentException("quantity cannot be null"));
    }

    @Generated
    public Integer getCopies() {
        return copies;
    }

    @Generated
    public void setCopies(final Integer copies) {
        this.copies = Optional.ofNullable(copies)
                .orElseThrow(() -> new IllegalArgumentException("copies cannot be null"));
    }

    @Generated
    public boolean isShouldCreate() {
        return shouldCreate;
    }

    @Generated
    public void setShouldCreate(final boolean shouldCreate) {
        this.shouldCreate = shouldCreate;
    }

    @Generated
    public boolean isShouldDelete() {
        return shouldDelete;
    }

    @Generated
    public void setShouldDelete(final boolean shouldDelete) {
        this.shouldDelete = shouldDelete;
    }
}
