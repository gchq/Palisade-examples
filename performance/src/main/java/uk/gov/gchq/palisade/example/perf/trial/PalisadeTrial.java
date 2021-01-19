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

package uk.gov.gchq.palisade.example.perf.trial;

import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.perf.client.SimpleClient;
import uk.gov.gchq.palisade.resource.LeafResource;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class PalisadeTrial extends PerfTrial {
    private static final Logger LOGGER = LoggerFactory.getLogger(PalisadeTrial.class);
    private final SimpleClient client;

    /**
     * Default constructor
     *
     * @param client a pre-configured client (eg. userId Alice and purpose SALARY for Employee Avro files)
     */
    protected PalisadeTrial(final SimpleClient client) {
        this.client = client;
    }


    protected void read(final String fileName) {
        Sink<Employee, CompletionStage<Long>> countingSink = Sink.fold(0L, (count, ignored) -> count + 1L);
        CompletionStage<Long> recordCount = Source.completionStage(client.register(fileName))
                .flatMapConcat(token -> client.fetch(token).zip(Source.repeat(token)))
                .map(resourceTokenPair -> client.read(resourceTokenPair.second(), resourceTokenPair.first()))
                .flatMapConcat(runnable -> runnable.run(SimpleClient.MATERIALIZER))
                .runWith(countingSink, SimpleClient.MATERIALIZER);
        try {
            LOGGER.info("Sink read consumed {} records", recordCount.toCompletableFuture().get(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error("Test timed out after 10 seconds", e);
        }
    }

    protected void query(final String fileName) {
        Sink<LeafResource, CompletionStage<Long>> countingSink = Sink.fold(0L, (count, ignored) -> count + 1L);
        CompletionStage<Long> resourceCount = Source.completionStage(client.register(fileName))
                .flatMapConcat(client::fetch)
                .runWith(countingSink, SimpleClient.MATERIALIZER);
        try {
            LOGGER.info("Sink query consumed {} resources", resourceCount.toCompletableFuture().get(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error("Test timed out after 10 seconds", e);
        }
    }

    protected void register(final String fileName) {
        Sink<String, CompletionStage<Long>> countingSink = Sink.fold(0L, (count, ignored) -> count + 1L);
        CompletionStage<Long> tokenCount = Source.completionStage(client.register(fileName))
                .runWith(countingSink, SimpleClient.MATERIALIZER);
        try {
            LOGGER.info("Sink register consumed {} tokens", tokenCount.toCompletableFuture().get(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error("Test timed out after 10 seconds", e);
        }
    }
}
