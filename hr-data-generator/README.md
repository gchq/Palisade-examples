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