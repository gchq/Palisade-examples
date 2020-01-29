# Local JVM Example

This example demonstrates different users querying an avro file over a REST api running locally in JVMs.

The example runs several different queries by the different users, with different purposes. When you run the example you will see the data has been redacted in line with the rules.  
For an overview of the example see [here](../../README.md).

In order to successfully run the Local JVM example please make sure that at least the Palisade-services and Palisade-examples repositories have been cloned from GitHub to the intended project location.

To run the example locally in JVMs follow these steps (from the root of the project):

1. Compile the code for both the services and examples code:
    ```bash
    mvn clean install
    ```
 
1.  Make sure you are within the Palisade-examples directory:
     ```bash
       cd $ProjectPath/Palisade-examples
     ```

1. Start the palisade services using the services manager. This will start all the palisade services on the JVM and remain open within the terminal window until it is closed using Ctrl + C:

    ```bash
      ./deployment/local-jvm/bash-scripts/runServiceLauncher.sh
    ```
   
1. It will take a couple of minutes for the services to start up. The status of this can be checked by going to http://localhost:8083. There should be 7 services into total to register with Eureka
    - Audit Service, Port 8081
    - Data-Service, Port 8082
    - Discovery-Service, Port 8083
    - Palisade-service, Port 8084
    - Policy-service, Port 8085
    - Resource-service, Port 8086
    - User-service, Port 8087
    
1. Open up a new terminal window/tab and populate Palisade with the example data. Make sure you are still within the Palisade-examples directory:
    ```bash
      ./deployment/local-jvm/bash-scripts/configureExamples.sh
    ```
   
   If this has been successful you will see the following output at the end of the process:
   ```bash
      The example users, data access policies, resource(s) and serialiser details have been initialised.
   ```
   If there is an issue and you have not seen this message then check that an instance for the data-service has been returned in the output
   ```bash
      Number of data-service instances found: 1
   ```
   If there are 0 instances of the data-service then this step will need to be done again. Exit the process using Ctrl + C and then run the script again

1. Run the example:
    ```bash
      ./deployment/local-jvm/bash-scripts/runLocalJVMExample.sh
    ```
   Or for an easier to read output:
    ```bash
      ./deployment/local-jvm/bash-scripts/runFormattedLocalJVMExample.sh
    ```     
    
    This just runs the java class: `uk.gov.gchq.palisade.example.runner.RestExample`. You can just run this class directly in your IDE.

1. Stop the REST services. Go to the open terminal window that was used to start the services and use Ctrl + C to exit the process. This will close all the services.

