/*
 * Copyright 2021 Crown Copyright
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

import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.palisade.client.akka.AkkaClient;
import uk.gov.gchq.palisade.data.serialise.Serialiser;
import uk.gov.gchq.palisade.resource.LeafResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

public class AkkaClientWrapper<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaClientWrapper.class);
    private final AkkaClient client;
    private final Serialiser<T> serialiser;
    private final Materializer materializer;

    public AkkaClientWrapper(final AkkaClient client, final Serialiser<T> serialiser, final Materializer materializer) {
        this.client = client;
        this.serialiser = serialiser;
        this.materializer = materializer;
    }

    public <M> Function<Sink<T, M>, M> execute(final String userId, final String fileName, final String purpose) {
        String token = this.register(userId, fileName, purpose).toCompletableFuture().join();
        Source<LeafResource, NotUsed> resources = this.fetch(token);
        Source<T, NotUsed> records = resources.flatMapConcat(resource -> Source.fromJavaStream(() -> this.read(token, resource)));
        return sink -> records.runWith(sink, materializer);
    }

    public CompletionStage<String> register(final String userId, final String fileName, final String purpose) {
        return client.register(userId, fileNameToResourceId(fileName), Collections.singletonMap("purpose", purpose));
    }

    public Source<LeafResource, NotUsed> fetch(final String token) {
        return client.fetchSource(token)
                .mapMaterializedValue(ignored -> NotUsed.notUsed());
    }

    public Stream<T> read(final String token, final LeafResource resource) throws IOException {
        return serialiser.deserialise(client.read(token, resource));
    }

    private static String fileNameToResourceId(final String fileName) {
        File file;
        if (!Path.of(fileName).isAbsolute()) {
            try {
                file = new File(fileName).getCanonicalFile();
            } catch (IOException ex) {
                LOGGER.warn("Failed to get CanonicalFile for '{}', using AbsoluteFile instead", fileName, ex);
                file = new File(fileName).getAbsoluteFile();
            }
        } else {
            file = new File(fileName);
        }

        String resourceId = file.toURI().toString();
        if ((fileName.endsWith("/") || fileName.endsWith("\\")) && !resourceId.endsWith("/")) {
            resourceId += "/";
        }
        return resourceId;
    }
}
