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

package uk.gov.gchq.palisade.example.perf.util;

/**
 * Wrapper around RuntimeException, should capture any exception thrown during performance trials.
 * This allows using a functional style wherever possible and elevating Exceptions to RuntimeExceptions.
 * Should a performance-test throw an exception, it is likely the system is not working correctly and any
 * results produced by the test suite will be invalid anyway, so fail-fast and report this error, never
 * handle it 'gracefully'.
 */
public class PerfException extends RuntimeException {
    // Hide the empty-constructor, we always want some info of what happened
    // Also hide the String constructor, we should only really be failing if an exception was thrown
    private PerfException() {
        super();
    }

    /**
     * Attach a message to a thrown exception, such as probable cause and action that resulted in the exception
     *
     * @param message a message to attach to enrich the context of the thrown exception cause
     * @param cause   the caught-and-rethrown exception (possibly not yet a RuntimeException)
     */
    public PerfException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Rethrow a caught exception as a RuntimeException
     *
     * @param cause the caught-and-rethrown exception (possibly not yet a RuntimeException)
     */
    public PerfException(final Throwable cause) {
        super(cause);
    }

    /**
     * Fine-grained control over exception output
     *
     * @param message            a message to attach to enrich the context of the thrown exception cause
     * @param cause              the caught-and-rethrown exception (possibly not yet a RuntimeException)
     * @param enableSuppression  whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public PerfException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
