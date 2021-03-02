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

# Performance

Palisade includes a performance tool for testing some simple scenarios. It uses the example rules and generates some fake HR data using the [HR data generator](../hr-data-generator/README.md).

The tool works on the following data-set sizes:

* *large* - 10,000 records in a single ~20MB file resource
* *small* - 1,000 records in a single ~2MB file resource
* *many* - 1 record per resource, with 10,000 such unique resources

Each data-set is then duplicated as the following variants:

* *no-policy* - a directory with no such rules applied (in reality, a single do-nothing rule is required)
* *with-policy* - a directory subject to a number of example rules

The following trials are tested for each size and variant of data-set:

* *read-native* - baseline test of a native file-read and deserialise, used for normalisation
* *request* - test of just palisade service with no data read or deserialise, providing an indication of palisade latency
* *read* - test of palisade and data services read, deserialise and coarse/fine-grain rule application, providing an indication of palisade overhead

The general naming scheme is "trial_size_variant".

All above values and more can be tweaked through the [config yaml](src/main/resources/application.yaml).

## Usage

### Automated

For an automated way to perform these tests, see the [services-manager](https://github.com/gchq/Palisade-services/blob/develop/services-manager/README.md) for more details.

Create some test data for the performance-test, start the palisade-services and run the performance-test:  
`java -jar -Dspring.profiles.active=example-perf services-manager/target/services-manager-*-exec.jar --manager.schedule=performance-create-task,palisade-task,performance-test-task`

* Services will start up with their cache/persistence-store prepopulated with example data
* The performance-test will run once all services have started
* Check `performance-test.log` for output data

Once the create-perf-data task has been run once, it does not need to be re-run:

* If running the performance tests repeatedly, the above command can be sped up to the default configuration of:  
  `java -jar -Dspring.profiles.active=example-perf services-manager/target/services-manager-*-exec.jar`
* If the palisade services are also still running, the above command can be sped up again to exclude starting the already-running services:  
  `java -jar -Dspring.profiles.active=example-perf services-manager/target/services-manager-*-exec.jar --manager.schedule=performance-test-task`  
  Or run just the performance-test manually as below...

### Manual

#### Creation of test data

Create a collection of Employee records in the [resources directory](/resources/data)

```bash
java -jar performance/target/performance-*-exec.jar --performance.action=create
# or similarly
java -Dspring.profiles.active=create -jar performance/target/performance-*-exec.jar
```

This may take a long time to run, depending upon the requested sizes of the test data (up to 5 minutes).

#### Running performance tests

Ensure first the [Palisade services](https://github.com/gchq/Palisade-services/) are running, and have been populated with the appropriate example data. The profile for prepopulating the services can be
found [here](../example-library/src/main/resources/application-example-perf.yaml).

Once all services have started, run the following:

```bash
java -jar performance/target/performance-*-exec.jar
```

Again, this may take some time, depending upon test data size. Be aware of any running antivirus software that may scan files in real time - eg. McAfee will contribute a factor of ~5x slow-down to bulk file tests.

### Analysis of results

The tool reports several statistics, but the most useful are the norm, mean and standard deviation. The percentage columns are the various percentile levels. The "Norm" column is the normalised column, showing how long various tests took compared to
reading the files natively (without Palisade). Reads of `large`, `small` and `many` files are normalised against their corresponding native read. Requests for `with-policy` are normalised against their corresponding `no-policy` requests.

### Sample of performance test results (small=1000, large=10000, many=100)

| Version   | Trial Name                | # Trials | Min      | Max      | Mean     | Std.dev. | 25%      | 50%      | 75%      | 99%      | Norm
|:----------|:--------------------------|:---------|:---------|:---------|:---------|:---------|:---------|:---------|:---------|:---------|:---------
| 0.4.0 jvm | read_large_native         | 5        | 0.416    | 0.430    | 0.425    | 0.005    | 0.424    | 0.428    | 0.429    | 0.430    | 1.000
|           | read_large_no_policy      | 5        | 0.475    | 0.537    | 0.493    | 0.022    | 0.479    | 0.486    | 0.488    | 0.535    | 1.159
|           | read_large_with_policy    | 5        | 1.166    | 1.636    | 1.332    | 0.170    | 1.227    | 1.236    | 1.394    | 1.627    | 3.132
|           | read_many_native          | 5        | 0.062    | 0.104    | 0.074    | 0.015    | 0.065    | 0.067    | 0.069    | 0.103    | 1.000
|           | read_many_no_policy       | 5        | 4.102    | 7.095    | 5.193    | 1.071    | 4.492    | 4.674    | 5.601    | 7.036    | 70.642
|           | read_many_with_policy     | 5        | 4.799    | 5.740    | 5.273    | 0.309    | 5.133    | 5.303    | 5.390    | 5.726    | 71.731
|           | read_small_native         | 5        | 0.062    | 0.085    | 0.071    | 0.009    | 0.065    | 0.065    | 0.078    | 0.085    | 1.000
|           | read_small_no_policy      | 5        | 0.131    | 0.170    | 0.154    | 0.013    | 0.150    | 0.158    | 0.163    | 0.170    | 2.169
|           | read_small_with_policy    | 5        | 0.189    | 0.247    | 0.222    | 0.023    | 0.200    | 0.232    | 0.242    | 0.247    | 3.119
|           | request_large_no_policy   | 5        | 0.039    | 0.980    | 0.261    | 0.360    | 0.083    | 0.091    | 0.114    | 0.945    | 1.000
|           | request_large_with_policy | 5        | 0.037    | 0.055    | 0.044    | 0.007    | 0.038    | 0.045    | 0.047    | 0.055    | 0.170
|           | request_many_no_policy    | 5        | 2.277    | 2.665    | 2.490    | 0.140    | 2.384    | 2.533    | 2.589    | 2.662    | 1.000
|           | request_many_with_policy  | 5        | 1.927    | 2.636    | 2.219    | 0.282    | 1.991    | 2.069    | 2.473    | 2.630    | 0.891
|           | request_small_no_policy   | 5        | 0.049    | 0.116    | 0.071    | 0.024    | 0.056    | 0.057    | 0.074    | 0.115    | 1.000
|           | request_small_with_policy | 5        | 0.046    | 0.136    | 0.096    | 0.029    | 0.097    | 0.098    | 0.105    | 0.134    | 1.366
|           |                           |          |          |          |          |          |          |          |          |          |
| 0.4.0 k8s | read_large_native         | 5        | 0.396    | 0.403    | 0.399    | 0.003    | 0.397    | 0.398    | 0.402    | 0.403    | 1.000
|           | read_large_no_policy      | 5        | 0.448    | 0.542    | 0.474    | 0.035    | 0.448    | 0.455    | 0.475    | 0.539    | 1.186
|           | read_large_with_policy    | 5        | 1.751    | 2.294    | 1.968    | 0.192    | 1.810    | 1.939    | 2.046    | 2.284    | 4.930
|           | read_many_native          | 5        | 0.063    | 0.092    | 0.071    | 0.011    | 0.064    | 0.065    | 0.069    | 0.091    | 1.000
|           | read_many_no_policy       | 5        | 5.788    | 6.624    | 6.293    | 0.302    | 6.126    | 6.415    | 6.513    | 6.620    | 89.146
|           | read_many_with_policy     | 5        | 6.580    | 8.424    | 7.253    | 0.623    | 6.995    | 7.029    | 7.237    | 8.377    | 102.747
|           | read_small_native         | 5        | 0.049    | 0.067    | 0.059    | 0.007    | 0.053    | 0.061    | 0.064    | 0.066    | 1.000
|           | read_small_no_policy      | 5        | 0.080    | 0.218    | 0.162    | 0.045    | 0.170    | 0.171    | 0.172    | 0.216    | 2.760
|           | read_small_with_policy    | 5        | 0.272    | 0.357    | 0.311    | 0.029    | 0.293    | 0.309    | 0.321    | 0.356    | 5.291
|           | request_large_no_policy   | 5        | 0.053    | 0.073    | 0.061    | 0.008    | 0.055    | 0.057    | 0.066    | 0.073    | 1.000
|           | request_large_with_policy | 5        | 0.055    | 0.072    | 0.066    | 0.006    | 0.066    | 0.067    | 0.071    | 0.072    | 1.087
|           | request_many_no_policy    | 5        | 3.155    | 4.157    | 3.591    | 0.350    | 3.294    | 3.647    | 3.703    | 4.139    | 1.000
|           | request_many_with_policy  | 5        | 3.215    | 7.265    | 4.572    | 1.656    | 3.298    | 3.319    | 5.762    | 7.205    | 1.273
|           | request_small_no_policy   | 5        | 0.050    | 0.081    | 0.065    | 0.011    | 0.057    | 0.064    | 0.074    | 0.081    | 1.000
|           | request_small_with_policy | 5        | 0.047    | 0.080    | 0.058    | 0.011    | 0.052    | 0.054    | 0.055    | 0.079    | 0.883
|           |                           |          |          |          |          |          |          |          |          |          |
| 0.5.0 k8s | read_large_native         | 5        | 0.534    | 0.606    | 0.572    | 0.028    | 0.555    | 0.563    | 0.603    | 0.606    | 1.000
|           | read_large_no_policy      | 5        | 2.062    | 2.353    | 2.176    | 0.107    | 2.097    | 2.124    | 2.243    | 2.348    | 3.803
|           | read_large_with_policy    | 5        | 1.889    | 2.428    | 2.087    | 0.181    | 2.006    | 2.055    | 2.057    | 2.413    | 3.648
|           | read_many_native          | 5        | 0.071    | 0.331    | 0.154    | 0.094    | 0.076    | 0.137    | 0.154    | 0.324    | 1.000
|           | read_many_no_policy       | 5        | 1.565    | 3.236    | 2.455    | 0.531    | 2.452    | 2.467    | 2.553    | 3.209    | 15.940
|           | read_many_with_policy     | 5        | 1.354    | 1.765    | 1.538    | 0.152    | 1.412    | 1.509    | 1.652    | 1.761    | 9.989
|           | read_small_native         | 5        | 0.063    | 0.069    | 0.066    | 0.002    | 0.066    | 0.067    | 0.067    | 0.069    | 1.000
|           | read_small_no_policy      | 5        | 0.372    | 0.850    | 0.606    | 0.171    | 0.477    | 0.598    | 0.731    | 0.845    | 9.146
|           | read_small_with_policy    | 5        | 0.318    | 0.471    | 0.384    | 0.059    | 0.330    | 0.366    | 0.434    | 0.469    | 5.798
|           | request_large_no_policy   | 5        | 0.160    | 0.275    | 0.204    | 0.044    | 0.174    | 0.174    | 0.235    | 0.274    | 1.000
|           | request_large_with_policy | 5        | 0.156    | 0.326    | 0.210    | 0.061    | 0.174    | 0.187    | 0.204    | 0.321    | 1.028
|           | request_many_no_policy    | 5        | 0.896    | 1.646    | 1.181    | 0.257    | 1.005    | 1.158    | 1.200    | 1.628    | 1.000
|           | request_many_with_policy  | 5        | 0.766    | 1.017    | 0.914    | 0.081    | 0.924    | 0.927    | 0.933    | 1.014    | 0.774
|           | request_small_no_policy   | 5        | 0.159    | 0.274    | 0.195    | 0.044    | 0.165    | 0.165    | 0.214    | 0.272    | 1.000
|           | request_small_with_policy | 5        | 0.169    | 0.319    | 0.239    | 0.054    | 0.192    | 0.252    | 0.265    | 0.317    | 1.225
