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

    private static final String DISCOVERY_URL = "http://localhost:8083/";
    private static ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConfigurator.class);
    private final String file;
    private final HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_2).build();

    private List<ServiceInstance> dataServiceInstances;
    private List<ServiceInstance> policyServiceInstances;
    private List<ServiceInstance> resourceServiceInstances;
    private List<ServiceInstance> userServiceInstances;

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
        getServiceInstanceLists();
        initialiseExample();

    }

    private void initialiseExample() {
        List<CompletableFuture<Boolean>> statuses = new ArrayList<>();

        addUsers(statuses);
        addResources(statuses);
        addPolicies(statuses);
        addSerialiser(statuses);

        // Wait for the users, policies, resources and serialisers to be loaded
        CompletableFuture.allOf(statuses.get(0), statuses.get(1), statuses.get(2), statuses.get(3), statuses.get(4), statuses.get(5)).join();
        LOGGER.info("The example users, data access policies, resource(s) and serialiser details have been initialised.");
    }

    private void addUsers(final List<CompletableFuture<Boolean>> futureList) {
        // Add the users to the User-service
        LOGGER.info("ADDING USERS");
        LOGGER.info("");

        final CompletableFuture<Boolean> userAliceStatus = sendRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getAlice()),
                userServiceInstances,
                "/addUser"
        );
        futureList.add(userAliceStatus);
        LOGGER.info("Alice added to the User-service");
        LOGGER.info("----------");
        LOGGER.info("");

        final CompletableFuture<Boolean> userBobStatus = sendRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getBob()),
                userServiceInstances,
                "/addUser"
        );
        futureList.add(userBobStatus);
        LOGGER.info("Bob added to the User-service");
        LOGGER.info("----------");
        LOGGER.info("");

        final CompletableFuture<Boolean> userEveStatus = sendRequest(
                AddUserRequest.create(new RequestId().id(UUID.randomUUID().toString())).withUser(ExampleUsers.getEve()),
                userServiceInstances,
                "/addUser"
        );
        futureList.add(userEveStatus);
        LOGGER.info("Eve added to the User-service");
        LOGGER.info("----------");
        LOGGER.info("");

    }

    private void addResources(final List<CompletableFuture<Boolean>> futureList) {
        // Add the resource to the Resource-service
        LOGGER.info("ADDING RESOURCES");
        LOGGER.info("");

        FileResource resource = createFileResource(file);
        CompletableFuture<Boolean> resourceStatus = new CompletableFuture<>();

        for (ServiceInstance instance : dataServiceInstances) {
            StringBuilder uri = new StringBuilder();

            if (instance.isSecure()) {
                uri.append("https://").append(instance.getHost()).append(":").append(instance.getMetadata().get("management.port"));
            } else {
                uri.append("http://").append(instance.getHost()).append(":").append(instance.getMetadata().get("management.port"));
            }

            final AddResourceRequest resourceRequest = new AddResourceRequest()
                    .resource(resource)
                    .connectionDetail(new SimpleConnectionDetail().uri(String.valueOf(uri)));
            resourceStatus = sendRequest(resourceRequest, resourceServiceInstances, "/addResource");
            LOGGER.info("Example resources added to the Resource-service");
            LOGGER.info("----------");
            LOGGER.info("");
        }
        futureList.add(resourceStatus);

    }

    private void addPolicies(final List<CompletableFuture<Boolean>> futureList) {
        // Using Custom Rule implementations
        LOGGER.info("ADDING POLICIES");
        LOGGER.info("");

        final SetResourcePolicyRequest customPolicies = ExamplePolicies.getExamplePolicy(file);
        final CompletableFuture<Boolean> policyStatus = sendRequest(customPolicies, policyServiceInstances, "/setResourcePolicySync");
        LOGGER.info("Example resource policies added to the Policy-service");
        LOGGER.info("----------");
        LOGGER.info("");
        futureList.add(policyStatus);

    }

    private void addSerialiser(final List<CompletableFuture<Boolean>> futureList) {
        // Add the Avro serialiser to the data-service
        LOGGER.info("ADDING SERIALISERS");
        LOGGER.info("");

        CompletableFuture<Boolean> serialiserStatus = createAvroSerialiser();
        LOGGER.info("Example serialiser added to Data-service cache");
        LOGGER.info("----------");
        LOGGER.info("");
        futureList.add(serialiserStatus);

    }

    private void getServiceInstanceLists() {
        LOGGER.info("");
        LOGGER.info("GETTING INSTANCES FOR SERVICES FROM EUREKA");
        LOGGER.info("");
        dataServiceInstances = getServiceInstances("data-service");
        policyServiceInstances = getServiceInstances("policy-service");
        resourceServiceInstances = getServiceInstances("resource-service");
        userServiceInstances = getServiceInstances("user-service");
        LOGGER.info("Eureka instances acquired");
        LOGGER.info("----------");
        LOGGER.info("");
    }

    private String getServiceUri(final List<ServiceInstance> instances) {
        ServiceInstance instance = instances.get(0);
        if (instance.isSecure()) {
            return "https://" + instance.getHost() + ":" + instance.getMetadata().get("management.port");
        } else {
            return "http://" + instance.getHost() + ":" + instance.getMetadata().get("management.port");
        }
    }

    private CompletableFuture<Boolean> sendRequest(final Request request, final List<ServiceInstance> instances, final String endpoint) {
        String requestString = requestToString(request);
        URI uri = URI.create(getServiceUri(instances) + endpoint);
        LOGGER.info("Sending request to {}", uri.toString());
        HttpRequest httpRequest;

        if ("/setResourcePolicySync".equals(endpoint)) {
            httpRequest = HttpRequest.newBuilder()
                    .PUT(BodyPublishers.ofString(requestString))
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
        } else {
            httpRequest = HttpRequest.newBuilder()
                    .POST(BodyPublishers.ofString(requestString))
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
        }

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Boolean.valueOf(response.body()));
    }

    private List<ServiceInstance> getServiceInstances(final String name) {
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();

        URI uri = URI.create(DISCOVERY_URL + "service-instances/" + name);
        LOGGER.info("Requesting information from Eureka for {}", name);
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
                LOGGER.error("Error occurred while processing response: {}", ex.getMessage());
            }
        }

        LOGGER.info("{} instances found: {}", name, serviceInstanceList.size());
        LOGGER.info("");
        return serviceInstanceList;
    }

    private CompletableFuture<Boolean> createAvroSerialiser() {
        DataFlavour dataFlavour = DataFlavour.of("employee", "avro");
        AvroSerialiser<Employee> serialiser = new AvroSerialiser<>(Employee.class);
        AddSerialiserRequest request = new AddSerialiserRequest().dataFlavour(dataFlavour).serialiser(serialiser);
        return sendRequest(request, dataServiceInstances, "/addSerialiser");
    }

    private List<String> processResponse(final String value) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(value);
        } catch (JSONException ex) {
            LOGGER.error("Error creating JSONArray: {}", ex.getMessage());
        }

        final JSONArray finalJsonArray = jsonArray;
        if (finalJsonArray != null) {
            return IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> ((JSONObject) finalJsonArray.get(index)).optString("instanceInfo")).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
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
        String str = path;
        List<DirectoryResource> resourceList = new ArrayList<>();
        List<String> pathList = new ArrayList<>();

        do {
            pathList.add(str);
            str = str.substring(0, str.lastIndexOf("/"));
        } while (!str.endsWith("//"));

        for (String s : pathList) {
            DirectoryResource parentResource = addParentResource(s);
            if (!resourceList.isEmpty()) {
                resourceList.get(resourceList.size() - 1).setParent(parentResource);
            }
            resourceList.add(parentResource);
        }
        resourceList.get(resourceList.size() - 1).setParent(createSystemResource(str));

        return resourceList.get(0);
    }

    private DirectoryResource addParentResource(final String path) {
        return new DirectoryResource().id(path);
    }

    private SystemResource createSystemResource(final String path) {
        return new SystemResource().id(path);
    }
}
