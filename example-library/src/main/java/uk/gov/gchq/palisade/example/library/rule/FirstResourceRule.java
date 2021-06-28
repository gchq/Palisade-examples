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

package uk.gov.gchq.palisade.example.library.rule;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.example.library.common.Role;
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.user.User;

import java.util.Objects;
import java.util.Set;

/**
 * A specific {@link Rule} implementation for the first returned resource
 */
public class FirstResourceRule implements Rule<Resource> {

    /**
     * Default constructor
     */
    public FirstResourceRule() {
        // no-args constructor
    }

    /**
     * Applies the {@link Rule} to a record
     *
     * @param resource the resource being processed
     * @param user the {@link User} making the request
     * @param context the {@link Context}, including the purpose, of the request
     * @return the {@link Resource} after the rule has been applied
     */
    public Resource apply(final Resource resource, final User user, final Context context) {

        Objects.requireNonNull(user);
        Objects.requireNonNull(context);
        Set<String> roles = user.getRoles();
        String fileId = resource.getId();
        String fileName = removeFileExtension(fileId);
        String lastChar = fileName.substring(fileName.length() - 1);

        if ("1".equals(lastChar)) {
            if (roles.contains(Role.HR.name())) {
                return resource;
            } else {
                return null;
            }
        } else {
            return resource;
        }
    }

    private String removeFileExtension(final String fileId) {
        return fileId.substring(0, fileId.lastIndexOf('.'));
    }

}
