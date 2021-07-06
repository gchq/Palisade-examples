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

package uk.gov.gchq.palisade.example.perf.trial;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.gov.gchq.palisade.example.perf.config.AkkaClientWrapper;
import uk.gov.gchq.palisade.example.perf.config.PerformanceConfiguration;
import uk.gov.gchq.palisade.resource.LeafResource;
import uk.gov.gchq.syntheticdatagenerator.types.Employee;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract class for all Palisade specific trials
 */
public abstract class AbstractPalisadeTrial extends AbstractPerfTrial {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPalisadeTrial.class);
    private static final long TIMEOUT_SECONDS = 30;
    private static final String TIMEOUT_MESSAGE = "Test timed out after " + TIMEOUT_SECONDS + " seconds";

    @Autowired
    private AkkaClientWrapper<Employee> client;
    @Autowired
    private ActorSystem actorSystem;
    @Autowired
    private PerformanceConfiguration performanceConfiguration;

    protected void read(final String fileName) {
        Sink<Employee, CompletionStage<Long>> countingSink = Sink.fold(0L, (count, ignored) -> count + 1L);
        CompletionStage<Long> recordCount = Source
                .completionStage(client.register(performanceConfiguration.getUserId(), fileName, performanceConfiguration.getPurpose()))
                .flatMapConcat(token -> client.fetch(token).zip(Source.repeat(token)))
                .flatMapConcat(resourceTokenPair -> Source.fromJavaStream(() -> client.read(resourceTokenPair.second(), resourceTokenPair.first())))
                .runWith(countingSink, () -> actorSystem);
        try {
            LOGGER.info("Sink read consumed {} records", recordCount.toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error(TIMEOUT_MESSAGE, e);
        }
    }

    protected void query(final String fileName) {
        Sink<LeafResource, CompletionStage<Long>> countingSink = Sink.fold(0L, (count, ignored) -> count + 1L);
        CompletionStage<Long> resourceCount = Source
                .completionStage(client.register(performanceConfiguration.getUserId(), fileName, performanceConfiguration.getPurpose()))
                .flatMapConcat(client::fetch)
                .runWith(countingSink, () -> actorSystem);
        try {
            LOGGER.info("Sink query consumed {} resources", resourceCount.toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error(TIMEOUT_MESSAGE, e);
        }
    }

    protected void register(final String fileName) {
        Sink<String, CompletionStage<Long>> countingSink = Sink.fold(0L, (count, ignored) -> count + 1L);
        CompletionStage<Long> tokenCount = Source
                .completionStage(client.register(performanceConfiguration.getUserId(), fileName, performanceConfiguration.getPurpose()))
                .runWith(countingSink, () -> actorSystem);
        try {
            LOGGER.info("Sink register consumed {} tokens", tokenCount.toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error(TIMEOUT_MESSAGE, e);
        }
    }
}
