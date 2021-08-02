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
It can be used to benchmark the performance in the local JVM and the local K8s environments. 
See the respective sections for more detail on the running of this performance test in these environments:
* [Local JVM Processes](../deployment-jvm/local-jvm/README.md)
* [Local Docker/Kubernetes Containers](../deployment-k8s/local-k8s/README.md)

Populating data records for the example will rely on the use of the [Synthetic Data Generator](https://github.com/gchq/synthetic-data-generator) for this task.

# Overview of the Performance Tests
The tool works on the following data-set sizes:

* *large* - 10,000 records in a single ~20MB file resource
* *small* - 1,000 records in a single ~2MB file resource
* *many* - 1 record per resource, with 10,000 such unique resources

Each data-set is then duplicated as the following variants:

* *no-policy* - a directory with no such rules applied (in reality, a single do-nothing rule is required) but most imporrtantly this doesnt require the data service to deserialise and then serialise the data
* *with-policy* - a directory subject to a number of example rules which therefore means that all the data needs to be deserialised to apply the rules and then the output data to be serialised to be sent to the client.

The following trials are tested for each size and variant of data-set:

* *read-native* - baseline test of a native file-read and deserialise, used for normalisation
* *request* - tests the client request for data and the return of what resources they are authorised to access, but doesnt include the call to the data service to get the actual data.
* *read* - tests the full process, including the read, deserialise and coarse/fine-grain rule application within the Data Service and then a deserialise of the data within the client, providing an indication of Palisade overhead

The general naming scheme is "trial_size_variant".

All of the above values can be tweaked through the [config yaml](src/main/resources/application.yaml).


### Analysis of results

The tool reports several statistics, but the most useful are the norm, mean and standard deviation.
The percentage columns are the various percentile levels.
The "Norm" column is the normalised column, showing how long various tests took compared to reading the files natively (without Palisade).
Reads of `large`, `small` and `many` files are normalised against their corresponding native read.
Requests for `with-policy` are normalised against their corresponding `no-policy` requests.

### Sample of performance test results (small = 1000 records in 1 resource, large = 10000 records in 1 resource, many = 1 record in each of 100 resources)

| Test                            |  # trials |        Min |        Max |       Mean |   Std.dev. |        25% |        50% |        75% |        99% |       Norm |
|:--------------------------------|:----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|:-----------|
| request_many_no_policy          |     5.000 |      0.282 |      0.449 |      0.355 |      0.055 |      0.323 |      0.360 |      0.362 |      0.445 |      1.000 |
| request_small_no_policy         |     5.000 |      0.048 |      0.062 |      0.056 |      0.006 |      0.049 |      0.058 |      0.060 |      0.062 |      1.000 |
| request_large_no_policy         |     5.000 |      0.050 |      0.061 |      0.058 |      0.004 |      0.057 |      0.060 |      0.060 |      0.061 |      1.000 |
| request_small_with_policy       |     5.000 |      0.049 |      0.151 |      0.072 |      0.040 |      0.050 |      0.053 |      0.059 |      0.148 |      1.302 |
| read_many_no_policy             |     5.000 |      0.494 |      0.582 |      0.525 |      0.034 |      0.502 |      0.504 |      0.545 |      0.581 |      9.561 |
| read_many_native                |     5.000 |      0.039 |      0.085 |      0.055 |      0.017 |      0.040 |      0.047 |      0.064 |      0.084 |      1.000 |
| read_small_no_policy            |     5.000 |      0.074 |      0.158 |      0.094 |      0.032 |      0.076 |      0.077 |      0.083 |      0.155 |      2.000 |
| read_small_with_policy          |     5.000 |      0.159 |      0.275 |      0.207 |      0.052 |      0.167 |      0.169 |      0.265 |      0.275 |      4.419 |
| read_large_with_policy          |     5.000 |      0.961 |      1.049 |      0.995 |      0.030 |      0.977 |      0.988 |      0.997 |      1.047 |      2.168 |
| read_many_with_policy           |     5.000 |      0.410 |      0.570 |      0.514 |      0.057 |      0.504 |      0.537 |      0.551 |      0.569 |      9.357 |
| read_small_native               |     5.000 |      0.047 |      0.047 |      0.047 |      0.000 |      0.047 |      0.047 |      0.047 |      0.047 |      1.000 |
| read_large_native               |     5.000 |      0.457 |      0.460 |      0.459 |      0.001 |      0.457 |      0.459 |      0.460 |      0.460 |      1.000 |
| request_many_with_policy        |     5.000 |      0.153 |      0.175 |      0.166 |      0.009 |      0.158 |      0.169 |      0.175 |      0.175 |      0.467 |
| request_large_with_policy       |     5.000 |      0.038 |      0.058 |      0.050 |      0.006 |      0.049 |      0.051 |      0.052 |      0.058 |      0.860 |
| read_large_no_policy            |     5.000 |      0.501 |      0.528 |      0.513 |      0.009 |      0.511 |      0.513 |      0.515 |      0.528 |      1.119 |