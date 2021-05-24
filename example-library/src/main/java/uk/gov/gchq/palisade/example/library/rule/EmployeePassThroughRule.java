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

package uk.gov.gchq.palisade.example.library.rule;

import uk.gov.gchq.palisade.Context;
import uk.gov.gchq.palisade.rule.Rule;
import uk.gov.gchq.palisade.user.User;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

/**
 * A simple rule implementation that does not redact any record fields
 */
public class EmployeePassThroughRule implements Rule<Employee> {
    private static final long serialVersionUID = 1L;

    /**
     * Applies the rule to a record
     *
     * @param record the record being processed
     * @param user the user making the request
     * @param context the context, including the purpose, of the request
     * @return the {@link Employee} record after the rule has been applied
     */
    public Employee apply(final Employee record, final User user, final Context context) {
        return record;
    }

    @Override
    public boolean isApplicable(final User user, final Context context) {
        return false;
    }

}
