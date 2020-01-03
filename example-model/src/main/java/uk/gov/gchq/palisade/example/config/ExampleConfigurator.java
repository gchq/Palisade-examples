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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;

import uk.gov.gchq.palisade.RequestId;
import uk.gov.gchq.palisade.example.common.ExamplePolicies;
import uk.gov.gchq.palisade.example.common.ExampleUsers;
import uk.gov.gchq.palisade.example.util.ExampleFileUtil;
import uk.gov.gchq.palisade.resource.impl.DirectoryResource;
import uk.gov.gchq.palisade.resource.impl.FileResource;
import uk.gov.gchq.palisade.resource.impl.SystemResource;
import uk.gov.gchq.palisade.resource.request.AddResourceRequest;
import uk.gov.gchq.palisade.service.SimpleConnectionDetail;
import uk.gov.gchq.palisade.service.policy.request.SetResourcePolicyRequest;
import uk.gov.gchq.palisade.service.request.Request;
import uk.gov.gchq.palisade.service.user.request.AddUserRequest;

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
    private static final String DISCOVERY_PORT = "8083/";
    private static final String POLICY_PORT = "8085/";
    private static final String RESOURCE_PORT = "8086/";
    private static final String USER_PORT = "8087/";
    private static ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);
    private final String file;
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();

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
        // The user authorisation owner or sys admin needs to add the user

        final CompletableFuture<Boolean> userAliceStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getAlice())
        );

        final CompletableFuture<Boolean> userBobStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getBob())
        );

        final CompletableFuture<Boolean> userEveStatus = addUserRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getEve())
        );

        // Using Custom Rule implementations
        final SetResourcePolicyRequest customPolicies = ExamplePolicies.getExamplePolicy(file);
        LOGGER.info(file);
        LOGGER.info(customPolicies.toString());
        final CompletableFuture<Boolean> policyStatus = addPolicyRequest(customPolicies);

        // The connection detail for the resource needs to be added
        LOGGER.info("Creating resource");
        FileResource resource = createFileResource(file);
        LOGGER.info("Getting service list from Eureka");
        List<ServiceInstance> serviceInstanceList = getServiceInstances();
        SimpleConnectionDetail connectionDetail = new SimpleConnectionDetail();

        //connectionDetail.setService(dataService);

        CompletableFuture<Boolean> resourceStatus = new CompletableFuture<>();
        for (ServiceInstance instance : serviceInstanceList) {
            LOGGER.info("Service found: {}", instance.getServiceId());
            final AddResourceRequest resourceRequest = new AddResourceRequest()
                    .resource(resource.serialisedFormat(file))
                    .connectionDetail(connectionDetail);
            LOGGER.info("Request created: {}", resourceRequest);
            resourceStatus = addResourceRequest(resourceRequest);
        }

        // Wait for the users, policies and resources to be loaded
        CompletableFuture.allOf(userAliceStatus, userBobStatus, userEveStatus, policyStatus, resourceStatus).join();
        LOGGER.info("The example users, data access policies and resource details have been initialised.");
    }

    private CompletableFuture<Boolean> addUserRequest(final AddUserRequest request) {
        LOGGER.info("Preparing to add user {} to the user service", request.user.getUserId().getId());
        String requestString = requestToString(request);
        LOGGER.info("Sending request to the user service");
        URI uri = URI.create(LOCALHOST + USER_PORT + "addUser");
        LOGGER.info(uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("");

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private CompletableFuture<Boolean> addPolicyRequest(final SetResourcePolicyRequest request) {
        LOGGER.info("Preparing to add resource policies to the policy service");
        String requestString = requestToString(request);
        LOGGER.info("Sending request to the policy service");
        URI uri = URI.create(LOCALHOST + POLICY_PORT + "setResourcePolicySync");
        LOGGER.info(uri.toString());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .PUT(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("");

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private CompletableFuture<Boolean> addResourceRequest(final AddResourceRequest request) {
        LOGGER.info("Preparing to add resource information to the resource service");
        String requestString = requestToString(request);
        LOGGER.info("Sending request to the resource service");
        URI uri = URI.create(LOCALHOST + RESOURCE_PORT + "addResource");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .PUT(BodyPublishers.ofString(requestString))
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        LOGGER.info("");

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private List<ServiceInstance> getServiceInstances() {
        LOGGER.info("Getting a list of instances for the data-service");
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();

        URI uri = URI.create(LOCALHOST + DISCOVERY_PORT + "service-instances/data-service");
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
                LOGGER.info(string);
                InstanceInfo instanceInfo = mapper.readValue(string, InstanceInfo.class);
                LOGGER.info(instanceInfo.toString());
                ServiceInstance serviceInstance = new EurekaServiceInstance(instanceInfo);
                serviceInstanceList.add(serviceInstance);
            } catch (Exception ex) {
                LOGGER.error("Error occurred during mapping: {}", ex.getMessage());
            }
        }

        return serviceInstanceList;
    }

    private List<String> processResponse(final String value) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(value);
        } catch (JSONException ex) {
            LOGGER.error("Error creating JSONArray: {}", ex.getMessage());
        }

        final JSONArray finalJsonArray = jsonArray;
        LOGGER.info(jsonArray.toString());
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> ((JSONObject) finalJsonArray.get(index)).optString("instanceInfo")).collect(Collectors.toList());
    }

    private String requestToString(final Request request) {
        try {
            LOGGER.info("Parsing request to String");
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOGGER.info("Parsing request to String failed");
            throw new RuntimeException(ex);
        }
    }

    private FileResource createFileResource(final String id) {
        LOGGER.info(id);
        String path = id.substring(0, id.lastIndexOf("/") + 1);
        LOGGER.info(path);
        FileResource file = new FileResource().id(id).type("");
        file.setParent(createParentResource(path));

        return file;
    }

    private DirectoryResource createParentResource(final String path) {
        LOGGER.info("File path: {}", path);
        DirectoryResource parent = new DirectoryResource();
        parent.id(path);
        String str = path.substring(0, path.lastIndexOf("/"));
        List<String> pathList = new ArrayList<>();
        List<DirectoryResource> resourceList = new ArrayList<>();

        do {
            pathList.add(str);
            str = str.substring(0, str.lastIndexOf("/"));
        } while (!str.endsWith("//"));

        LOGGER.info(pathList.toString());

        for (String s : pathList) {
            DirectoryResource resource = new DirectoryResource().id(s);
            resourceList.add(resource);
        }

        parent.parent(resourceList.get(1)
                .parent(resourceList.get(2)
                        .parent(resourceList.get(3)
                                .parent(resourceList.get(4)
                                        .parent(resourceList.get(5)
                                                .parent(resourceList.get(6)
                                                        .parent(resourceList.get(7)
                                                                .parent(createSystemResource(str)))))))));


        LOGGER.info(parent.toString());
        return parent;
    }

    private SystemResource createSystemResource(final String path) {
        return new SystemResource().id(path);
    }
}
