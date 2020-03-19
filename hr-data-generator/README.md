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
 
# HR Data Generator

This module contains the code for the HR Data examples. This includes generator code that can produce AVRO files of
synthetic HR data.

To use the generator navigate to the Palisade-examples directory and run:

```bash
mvn clean install
```

then to start the generator:

```bash
./deployment/bash-scripts/createHRData.sh PATH EMPLOYEES FILES [THREADS]
```

- PATH is the relative path to generate the files
- EMPLOYEES is the number of employee records to create
- FILES is the number of files to spread them over
- THREADS (optional) specifies the number of threads to use.

For example to generate 1,000,000 employee records, spread over 15 files, running the program with 15 threads, and writing the output files to /data/employee:

```bash
./deployment/bash-scripts/createHRData.sh /data/employee 1000000 15 15
```
