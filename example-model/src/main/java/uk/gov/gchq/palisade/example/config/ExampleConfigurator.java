/*
 * Copyright 2019 Crown Copyright
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
package uk.gov.gchq.palisade.example.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;

import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.data.serialise.AvroSerialiser;
import uk.gov.gchq.palisade.example.common.ExamplePolicies;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.hrdatagenerator.types.Employee;
import uk.gov.gchq.palisade.example.request.AddSerialiserRequest;
import uk.gov.gchq.palisade.example.request.AddUserRequest;
import uk.gov.gchq.palisade.example.request.SetResourcePolicyRequest;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.reader.common.DataFlavour;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SystemResource;
import uk.gov.gchq.palisade.resource.request.AddResourceRequest;
import uk.gov.gchq.palisade.service.CacheService;
import uk.gov.gchq.palisade.service.SimpleConnectionDetail;
import uk.gov.gchq.palisade.service.request.Request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Convenience class for the examples to configure the users and data access policies for the example.
 */
public final class ExampleConfigurator {

    private static final String LOCALHOST = "http://localhost:";
    private static final String DATA_PORT = "8082/";
    private static final String DISCOVERY_PORT = "8083/";
    private static final String POLICY_PORT = "8085/";
    private static final String RESOURCE_PORT = "8086/";
    private static final String USER_PORT = "8087/";
    private static ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);
    private final String file;
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();

    @Autowired
    private CacheService cacheService;

    /**
     * Establishes policies and details for the examples and writes these into the configuration service.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        new ExampleConfigurator(args[0]);
    }

    public ExampleConfigurator(final String file) {
        URI absoluteFileURI = ExampleFileUtil.convertToFileURI(file);
        this.file = absoluteFileURI.toString();
        try {
            initialiseExample();
        } catch (IOException ex) {
            LOGGER.debug("Error processing request {}", ex.getMessage());
        }

    }

    private void initialiseExample() throws IOException {
        // Add the users to the User-service
        LOGGER.info("");

        LOGGER.info("ADDING USERS");
        LOGGER.info("");

        final CompletableFuture<Boolean> userAliceStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getAlice())
        );
        LOGGER.info("Alice added to the User-service");
        LOGGER.info("----------");
        LOGGER.info("");

        final CompletableFuture<Boolean> userBobStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getBob())
        );
        LOGGER.info("Bob added to the User-service");
        LOGGER.info("----------");
        LOGGER.info("");

        final CompletableFuture<Boolean> userEveStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getEve())
        );
        LOGGER.info("Eve added to the User-service");
        LOGGER.info("----------");
        LOGGER.info("");

        LOGGER.info("GETTING DATA-SERVICE INFO");
        LOGGER.info("");

        // Get all the Data-service instances from Eureka
        LOGGER.info("Preparing to find Data-service instances through Eureka");
        List<ServiceInstance> serviceInstanceList = getServiceInstances();
        LOGGER.info("Number of data-service instances found: {}", serviceInstanceList.size());
        LOGGER.info("----------");
        LOGGER.info("");

        // Add the resource to the Resource-service
        LOGGER.info("ADDING RESOURCES");
        LOGGER.info("");

        FileResource resource = createFileResource(file);
        CompletableFuture<Boolean> resourceStatus = new CompletableFuture<>();

        for (ServiceInstance instance : serviceInstanceList) {
            final AddResourceRequest resourceRequest = new AddResourceRequest()
                    .resource(resource.serialisedFormat(file))
                    .connectionDetail(new SimpleConnectionDetail().uri("http://localhost:8082"));
            resourceStatus = addResourceRequest(resourceRequest);
            LOGGER.info("Example resources added to the Resource-service");
            LOGGER.info("----------");
            LOGGER.info("");
        }

        // Using Custom Rule implementations
        LOGGER.info("ADDING POLICIES");
        LOGGER.info("");

        final SetResourcePolicyRequest customPolicies = ExamplePolicies.getExamplePolicy(file);
        final CompletableFuture<Boolean> policyStatus = addPolicyRequest(customPolicies);
        LOGGER.info("Example resource policies added to the Policy-service");
        LOGGER.info("----------");
        LOGGER.info("");

        LOGGER.info("ADDING SERIALISERS");
        LOGGER.info("");

        CompletableFuture<Boolean> serialiserStatus = createAvroSerialiser();
        LOGGER.info("Example serialiser added to Data-service cache");
        LOGGER.info("----------");
        LOGGER.info("");

        // Wait for the users, policies and resources to be loaded
        CompletableFuture.allOf(userAliceStatus, userBobStatus, userEveStatus, policyStatus, resourceStatus, serialiserStatus).join();
        LOGGER.info("The example users, data access policies, resource(s) and serialiser details have been initialised.");
    }

    private CompletableFuture<Boolean> addUserRequest(final AddUserRequest request) {
        LOGGER.info("Adding {} to the user service", request.user.getUserId().getId());
        String requestString = requestToString(request);
        URI uri = URI.create(LOCALHOST + USER_PORT + "addUser");
        LOGGER.info("Sending user to {}", uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private CompletableFuture<Boolean> addPolicyRequest(final SetResourcePolicyRequest request) {
        LOGGER.info("Adding resource policies to the policy service");
        String requestString = requestToString(request);
        URI uri = URI.create(LOCALHOST + POLICY_PORT + "setResourcePolicySync");
        LOGGER.info("Sending policies to {}", uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .PUT(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private CompletableFuture<Boolean> addResourceRequest(final AddResourceRequest request) {
        LOGGER.info("Adding resource information to the resource service");
        String requestString = requestToString(request);
        URI uri = URI.create(LOCALHOST + RESOURCE_PORT + "addResource");
        LOGGER.info("Sending resources to {}", uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private CompletableFuture<Boolean> addSerialiserRequest(final AddSerialiserRequest request) {
        LOGGER.info("Adding serialiser information to the data service cache");
        String requestString = requestToString(request);
        URI uri = URI.create(LOCALHOST + DATA_PORT + "addSerialiser");
        LOGGER.info("Sending serialiser to {}", uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private List<ServiceInstance> getServiceInstances() {
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();

        URI uri = URI.create(LOCALHOST + DISCOVERY_PORT + "service-instances/data-service");
        LOGGER.info("Requesting information from Eureka - {}", uri.toString());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        String strResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> String.valueOf(response.body())).join();

        List<String> stringList = processResponse(strResponse);
        for (String string : stringList) {
            try {
                InstanceInfo instanceInfo = mapper.readValue(string, InstanceInfo.class);
                ServiceInstance serviceInstance = new EurekaServiceInstance(instanceInfo);
                serviceInstanceList.add(serviceInstance);
            } catch (Exception ex) {
                LOGGER.error("Error occurred during mapping: {}", ex.getMessage());
            }
        }

        return serviceInstanceList;
    }

    private CompletableFuture<Boolean> createAvroSerialiser() {
        DataFlavour dataFlavour = DataFlavour.of("employee", "avro");
        AvroSerialiser<Employee> serialiser = new AvroSerialiser<>(Employee.class);
        AddSerialiserRequest request = new AddSerialiserRequest().dataFlavour(dataFlavour).serialiser(serialiser);
        return addSerialiserRequest(request);
    }

    private List<String> processResponse(final String value) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(value);
        } catch (JSONException ex) {
            LOGGER.error("Error creating JSONArray: {}", ex.getMessage());
        }

        final JSONArray finalJsonArray = jsonArray;
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> ((JSONObject) finalJsonArray.get(index)).optString("instanceInfo")).collect(Collectors.toList());
    }

    private String requestToString(final Request request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOGGER.info("Parsing request to String failed");
            throw new RuntimeException(ex);
        }
    }

    private FileResource createFileResource(final String id) {
        String path = id.substring(0, id.lastIndexOf("/") + 1);
        FileResource file = new FileResource().id(id).serialisedFormat("avro").type("employee");
        file.setParent(createParentResource(path));

        return file;
    }

    private DirectoryResource createParentResource(final String path) {
        DirectoryResource parent = new DirectoryResource();
        parent.id(path);
        String str = path.substring(0, path.lastIndexOf("/"));
        List<String> pathList = new ArrayList<>();
        List<DirectoryResource> resourceList = new ArrayList<>();

        do {
            pathList.add(str);
            str = str.substring(0, str.lastIndexOf("/"));
        } while (!str.endsWith("//"));

        for (String s : pathList) {
            DirectoryResource resource = new DirectoryResource().id(s);
            resourceList.add(resource);
        }

        int size = pathList.size();

        parent.parent(resourceList.get(1)
                .parent(resourceList.get(2)
                        .parent(resourceList.get(3)
                                .parent(resourceList.get(4)
                                        .parent(resourceList.get(5)
                                                .parent(resourceList.get(6)
                                                        .parent(resourceList.get(7)
                                                                .parent(createSystemResource(str)))))))));

        return parent;
    }

    private DirectoryResource addParentResource(final String path) {
        return new DirectoryResource().id(path);
    }

    private SystemResource createSystemResource(final String path) {
        return new SystemResource().id(path);
    }
}
