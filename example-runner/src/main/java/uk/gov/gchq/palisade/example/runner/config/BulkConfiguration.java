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

public class BulkConfiguration {
    private String directory;
    private Integer quantity;
    private Integer copies;
    private Boolean shouldCreate;
    private Boolean shouldDelete;

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
    public Boolean getShouldCreate() {
        return shouldCreate;
    }

    @Generated
    public void setShouldCreate(final Boolean shouldCreate) {
        this.shouldCreate = Optional.ofNullable(shouldCreate)
                .orElseThrow(() -> new IllegalArgumentException("shouldCreate cannot be null"));
    }

    @Generated
    public Boolean getShouldDelete() {
        return shouldDelete;
    }

    @Generated
    public void setShouldDelete(final Boolean shouldDelete) {
        this.shouldDelete = Optional.ofNullable(shouldDelete)
                .orElseThrow(() -> new IllegalArgumentException("shouldDelete cannot be null"));
    }
}
