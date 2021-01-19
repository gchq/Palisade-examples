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

package uk.gov.gchq.palisade.example.perf.client;


import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.WebSocketRequest;
import akka.http.scaladsl.model.ws.TextMessage.Strict;
import akka.stream.Materializer;
import akka.stream.javadsl.BroadcastHub;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.StreamConverters;
import akka.util.ByteString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.serializer.support.SerializationFailedException;

import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.perf.client.model.DataRequest;
import uk.gov.gchq.palisade.example.perf.client.model.MessageType;
import uk.gov.gchq.palisade.example.perf.client.model.PalisadeRequest;
import uk.gov.gchq.palisade.example.perf.client.model.PalisadeResponse;
import uk.gov.gchq.palisade.example.perf.client.model.WebSocketMessage;
import uk.gov.gchq.palisade.example.perf.config.PerformanceConfiguration;
import uk.gov.gchq.palisade.resource.LeafResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletionStage;

public class SimpleClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ActorSystem ACTOR_SYSTEM = ActorSystem.create("client");
    public static final Materializer MATERIALIZER = Materializer.createMaterializer(ACTOR_SYSTEM);
    private static final Http HTTP = Http.get(ACTOR_SYSTEM);
    private static final AvroSerialiser<Employee> SERIALISER = new AvroSerialiser<>(Employee.class);

    private final String palisadeUrl;
    private final String filteredResourceUrl;
    private final PerformanceConfiguration performanceConfiguration;

    public SimpleClient(final String palisadeUrl, final String filteredResourceUrl, final PerformanceConfiguration performanceConfiguration) {
        this.palisadeUrl = palisadeUrl;
        this.filteredResourceUrl = filteredResourceUrl;
        this.performanceConfiguration = performanceConfiguration;
    }

    private static String fileNameToResourceId(final String fileName) {
        File file;
        if (!Path.of(fileName).isAbsolute()) {
            try {
                file = new File(fileName).getCanonicalFile();
            } catch (IOException ex) {
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

    private CompletionStage<String> register(final String userId, final String resourceId, final String purpose) {
        return HTTP.singleRequest(HttpRequest.POST(String.format("http://%s/api/registerDataRequest", palisadeUrl))
                .withEntity(ContentTypes.APPLICATION_JSON, serialize(
                        PalisadeRequest.Builder.create()
                                .withUserId(userId)
                                .withResourceId(resourceId)
                                .withContext(Collections.singletonMap("purpose", purpose))
                ).getBytes()))
                .thenApply(this::readHttpMessage)
                .thenApply(PalisadeResponse::getToken);
    }

    public CompletionStage<String> register(final String fileName) {
        return this.register(performanceConfiguration.getUserId(), fileNameToResourceId(fileName), performanceConfiguration.getPurpose());
    }

    public Source<LeafResource, NotUsed> fetch(final String token) {
        // Send out CTS messages after each RESOURCE
        var cts = WebSocketMessage.Builder.create().withType(MessageType.CTS).noHeaders().noBody();
        // Produces the output source of LeafResource, the BroadcastHub will have a single source materialised for it
        var splitter = Flow.<WebSocketMessage>create()
                .filter(msg -> msg.getType().equals(MessageType.RESOURCE))
                .map(msg -> msg.getBodyObject(LeafResource.class))
                .toMat(BroadcastHub.of(LeafResource.class), Keep.right());
        var repeater = Source.repeat(cts).take(105);
        // Map inbound messages to outbound CTS until COMPLETE is seen
        var business = Flow.<WebSocketMessage>create()
                .takeWhile(msg -> !msg.getType().equals(MessageType.COMPLETE))
                .viaMat(Flow.fromSinkAndSourceMat(splitter, repeater, Keep.left()), Keep.right());
        // Ser/Des the websocket
        var clientFlow = Flow.<Message>create()
                .map(this::readWsMessage)
                .viaMat(business, Keep.right())
                .map(this::writeWsMessage);
        // Make the request using the ser/des flow linked to the oscillator
        var wsResponse = HTTP.singleWebSocketRequest(
                WebSocketRequest.create(String.format("ws://%s/resource/%s", filteredResourceUrl, token)),
                clientFlow,
                MATERIALIZER);
        // Once the wsUpgrade request completes, return the connected Bcast Source
        return wsResponse.first()
                .thenApply(upgrade -> wsResponse.second())
                .toCompletableFuture().join();
    }

    public RunnableGraph<Source<Employee, NotUsed>> read(final String token, final LeafResource resource) {
        return Source.completionStageSource(HTTP.singleRequest(
                HttpRequest.POST(String.format("http://%s/read/chunked", resource.getConnectionDetail().createConnection()))
                        .withEntity(ContentTypes.APPLICATION_JSON, serialize(
                                DataRequest.Builder.create()
                                        .withToken(token)
                                        .withLeafResourceId(resource.getId())
                        ).getBytes()))
                .thenApply(response -> response.entity().getDataBytes()))
                .toMat(StreamConverters.asInputStream(), Keep.right())
                .mapMaterializedValue(inputStream -> Source.fromJavaStream(() -> SERIALISER.deserialise(inputStream)));
    }


    private PalisadeResponse readHttpMessage(final HttpResponse message) {
        // Akka will sometimes convert a StrictMessage to a StreamedMessage, so we have to handle both cases here
        StringBuilder builder = message.entity().getDataBytes()
                .map(ByteString::utf8String)
                .runFold(new StringBuilder(), StringBuilder::append, ACTOR_SYSTEM)
                .toCompletableFuture().join();
        return deserialize(builder.toString(), PalisadeResponse.class);
    }

    private WebSocketMessage readWsMessage(final Message message) {
        // Akka will sometimes convert a StrictMessage to a StreamedMessage, so we have to handle both cases here
        StringBuilder builder = message.asTextMessage().getStreamedText()
                .runFold(new StringBuilder(), StringBuilder::append, ACTOR_SYSTEM)
                .toCompletableFuture().join();
        return deserialize(builder.toString(), WebSocketMessage.class);
    }

    private Message writeWsMessage(final WebSocketMessage message) {
        return new Strict(serialize(message));
    }

    private static <T> T deserialize(final String json, final Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new SerializationFailedException("Failed to write message", e);
        }
    }

    private static String serialize(final Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new SerializationFailedException("Failed to write message", e);
        }
    }
}
