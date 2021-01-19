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
package uk.gov.gchq.palisade.example.perf.client.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.gchq.palisade.Generated;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The {@link DataRequest} is the input for the data-service where the resource is read.
 * This message is created by the response from the filtered-resource-service to the client.
 * It is then routed via the resource's connectionDetail to the appropriate instance of a data-service.
 * This message is used to retrieve the {@link uk.gov.gchq.palisade.reader.request.DataReaderRequest} from the attribute-masking-service.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class DataRequest {

    private final String token;  // Unique identifier for the client's request
    private final String leafResourceId;  // Leaf Resource ID that that is being asked to access

    @JsonCreator
    private DataRequest(
            final @JsonProperty("token") String token,
            final @JsonProperty("leafResourceId") String leafResourceId) {

        this.token = Optional.ofNullable(token)
                .orElseThrow(() -> new IllegalArgumentException("token cannot be null"));
        this.leafResourceId = Optional.ofNullable(leafResourceId)
                .orElseThrow(() -> new IllegalArgumentException("leafResourceId cannot be null"));
    }

    @Generated
    public String getToken() {
        return token;
    }

    @Generated
    public String getLeafResourceId() {
        return leafResourceId;
    }

    /**
     * Builder class for the creation of instances of the DataRequest.
     * This is a variant of the Fluent Builder which will use Java Objects or JsonNodes equivalents for the components in the build.
     */
    public static class Builder {
        /**
         * Starter method for the Builder class.
         * This method is called to start the process of creating the DataRequest class.
         *
         * @return interface {@link IToken} for the next step in the build.
         */
        public static IToken create() {
            return token -> leafResourceId ->
                    new DataRequest(token, leafResourceId);
        }

        /**
         * Adds the token to the message
         */
        public interface IToken {
            /**
             * Adds the token to the request
             *
             * @param token the client's unique token
             * @return interface {@link ILeafResourceId} for the next step in the build.
             */
            ILeafResourceId withToken(String token);
        }

        /**
         * Adds the leaf resource id to the message
         */
        public interface ILeafResourceId {
            /**
             * Adds the leaf resource id to the request
             *
             * @param leafResourceId resource ID for the request.
             * @return the completed DataRequest object
             */
            DataRequest withLeafResourceId(String leafResourceId);
        }

    }

    @Override
    @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataRequest)) {
            return false;
        }
        final DataRequest that = (DataRequest) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(leafResourceId, that.leafResourceId);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(token, leafResourceId);
    }

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", DataRequest.class.getSimpleName() + "[", "]")
                .add("token='" + token + "'")
                .add("leafResourceId='" + leafResourceId + "'")
                .toString();
    }
}
