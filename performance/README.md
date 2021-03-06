<!---
Copyright 2018-2021 Crown Copyright

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--->
# <img src="../logos/logo.svg" width="180">

## A Tool for Complex and Scalable Data Access Policy Enforcement

# Performance

Palisade includes a performance tool for testing some simple scenarios.
It uses the example rules and generates some fake HR data using the [synthetic data generator](https://github.com/gchq/synthetic-data-generator).

The tool works on the following data-set sizes:

* *large* - 10,000 records in a single ~20MB file resource
* *small* - 1,000 records in a single ~2MB file resource
* *many* - 1 record per resource, with 10,000 such unique resources

Each data-set is then duplicated as the following variants:

* *no-policy* - a directory with no such rules applied (in reality, a single do-nothing rule is required)
* *with-policy* - a directory subject to a number of example rules

The following trials are tested for each size and variant of data-set:

* *read-native* - baseline test of a native file-read and deserialise, used for normalisation
* *request* - tests the client request without any deserialisation or rule application within the Data Service
* *read* - tests the full process, including the read, deserialise and coarse/fine-grain rule application within the Data Service, providing an indication of Palisade overhead

The general naming scheme is "trial_size_variant".

All above values and more can be tweaked through the [config yaml](src/main/resources/application.yaml).

## Usage

### Automated

For an automated way to perform these tests, see the [Services Manager](https://github.com/gchq/Palisade-services/blob/develop/services-manager/README.md) for more details.

Creates some test data for the performance tests, starts all the Palisade services and runs the performance tests:

1. Make sure you are within the Palisade-services directory
   ```bash
   >> ls
     drwxrwxrwx attribute-masking-service
     drwxrwxrwx audit-service
     drwxrwxrwx data-service
     drwxrwxrwx filtered-resource-service
     drwxrwxrwx palisade-service
     drwxrwxrwx policy-service
     drwxrwxrwx resource-service
     drwxrwxrwx services-manager
     drwxrwxrwx topic-offset-service
     drwxrwxrwx user-service
   ```
1. Then run the services manager:
    ```bash
    >> java -jar -Dspring.profiles.active=example-perf services-manager/target/services-manager-*-exec.jar --manager.schedule=performance-create-task,palisade-task,performance-test-task
    ```

* The services will start up with their cache/persistence stores pre-populated with example data
* The performance-test will run once all services have started
* Check `performance-test.log` for output data

Once the create-perf-data task has been run once, it does not need to be re-run:

* If running the performance tests repeatedly, the above command can be sped up to the default configuration of:  
  `java -jar -Dspring.profiles.active=example-perf services-manager/target/services-manager-*-exec.jar`
* If the Palisade services are also still running, the above command can be sped up again to exclude starting the already-running services:  
  `java -jar -Dspring.profiles.active=example-perf services-manager/target/services-manager-*-exec.jar --manager.schedule=performance-test-task`  
  Or run just the performance-test manually as below...

### Manual

#### Creation of test data

Create a collection of Employee records in the [resources directory](../resources/data)

```bash
>> java -jar performance/target/performance-*-exec.jar --performance.action=create
# or similarly
>> java -Dspring.profiles.active=create -jar performance/target/performance-*-exec.jar
```

This may take a long time to run, depending upon the requested sizes of the test data (up to 5 minutes).

#### Running performance tests

Ensure first the [Palisade services](https://github.com/gchq/Palisade-services/) are running, and have been populated with the appropriate example data.
This also includes ensuring Kafka and Redis are running and ready.
The profile for prepopulating the services can be found [here](../example-library/src/main/resources/application-example-perf.yaml).
The procedure for starting services can be followed from the [deployment-jvm](../deployment-jvm/README.md), which includes pre-populating and starting Kafka/Redis with docker-compose.

Once all services have started, run the following:

```bash
>> java -jar performance/target/performance-*-exec.jar
```

Again, this may take some time, depending upon test data size.
Be aware of any running antivirus software that may scan files in real time - eg. McAfee will contribute a factor of ~5x slow-down to bulk file tests.

### Analysis of results

The tool reports several statistics, but the most useful are the norm, mean and standard deviation.
The percentage columns are the various percentile levels.
The "Norm" column is the normalised column, showing how long various tests took compared to reading the files natively (without Palisade).
Reads of `large`, `small` and `many` files are normalised against their corresponding native read.
Requests for `with-policy` are normalised against their corresponding `no-policy` requests.

### Sample of performance test results (small = 1000 records in 1 resource, large = 10000 records in 1 resource, many = 1 record in each of 100 resources)

| Test                            |  # trials |        Min |        Max |       Mean |   Std.dev. |        25% |        50% |        75% |        99% |
|:--------------------------------|:----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|
| request_many_no_policy          |    10.000 |      1.098 |      1.625 |      1.380 |      0.177 |      1.251 |      1.379 |      1.539 |      1.620 |
| request_small_no_policy         |    10.000 |      0.111 |      0.350 |      0.222 |      0.078 |      0.158 |      0.210 |      0.262 |      0.350 |
| request_large_no_policy         |    10.000 |      0.076 |      0.439 |      0.200 |      0.097 |      0.144 |      0.164 |      0.233 |      0.425 |
| request_small_with_policy       |    10.000 |      0.157 |      0.366 |      0.231 |      0.066 |      0.186 |      0.201 |      0.274 |      0.362 |
| read_many_no_policy             |    10.000 |      1.109 |      2.947 |      2.152 |      0.544 |      1.898 |      2.194 |      2.577 |      2.931 |
| read_many_native                |    10.000 |      0.044 |      0.113 |      0.063 |      0.018 |      0.056 |      0.059 |      0.064 |      0.110 |
| read_small_no_policy            |    10.000 |      0.141 |      0.533 |      0.302 |      0.139 |      0.177 |      0.262 |      0.438 |      0.528 |
| read_small_with_policy          |    10.000 |      0.291 |      0.732 |      0.459 |      0.131 |      0.351 |      0.468 |      0.542 |      0.716 |
| read_large_with_policy          |    10.000 |      1.666 |      2.100 |      1.827 |      0.125 |      1.715 |      1.843 |      1.878 |      2.082 |
| read_many_with_policy           |    10.000 |      1.322 |      3.140 |      2.259 |      0.597 |      1.785 |      2.306 |      2.632 |      3.133 |
| read_small_native               |    10.000 |      0.047 |      0.075 |      0.058 |      0.007 |      0.055 |      0.058 |      0.060 |      0.074 |
| read_large_native               |    10.000 |      0.425 |      0.557 |      0.471 |      0.033 |      0.455 |      0.462 |      0.477 |      0.551 |
| request_many_with_policy        |    10.000 |      0.646 |      1.246 |      0.839 |      0.198 |      0.710 |      0.743 |      0.860 |      1.240 |
| request_large_with_policy       |    10.000 |      0.143 |      0.288 |      0.180 |      0.040 |      0.153 |      0.167 |      0.187 |      0.280 |
| read_large_no_policy            |    10.000 |      0.942 |      1.540 |      1.158 |      0.197 |      1.024 |      1.064 |      1.261 |      1.532 |
