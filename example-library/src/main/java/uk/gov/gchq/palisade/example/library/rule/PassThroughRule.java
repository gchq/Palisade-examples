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
import uk.gov.gchq.palisade.resource.Resource;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.user.User;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

/**
 * A simple {@link Rule} implementation that does not make any resource redactions
 */
public class PassThroughRule implements Rule<Resource> {
    private static final long serialVersionUID = 1L;

    /**
     * Applies the {@link Rule} to a record
     *
     * @param resource the resource being processed
     * @param user the {@link User} making the request
     * @param context the {@link Context}, including the purpose, of the request
     * @return the {@link Employee} record after the rule has been applied
     */
    public Resource apply(final Resource resource, final User user, final Context context) {
        return resource;
    }

    @Override
    public boolean isApplicable(final User user, final Context context) {
        return false;
    }
}
