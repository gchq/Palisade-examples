# Local JVM Example

This example demonstrates different users querying an avro file over a REST api running locally in JVMs.

The example runs several different queries by the different users, with different purposes. When you run the example you will see the data has been redacted in line with the rules.  
For an overview of the example see [here](../../README.md).

In order to successfully run the Local JVM example please make sure that at least the Palisade-services and Palisade-examples repositories have been cloned from GitHub to the intended project location.

To run the example locally in JVMs follow these steps (from the root of the project):

1. Compile the code for both the services and examples code:
    ```bash
    >> mvn clean install
    ```
 
1.  Make sure you are within the Palisade-services directory:
     ```bash
     >> cd $ProjectPath
     >> ls
       drwxrwxrwx Palisade-clients
       drwxrwxrwx Palisade-common
       drwxrwxrwx Palisade-examples
       drwxrwxrwx Palisade-integration-tests
       drwxrwxrwx Palisade-readers
       drwxrwxrwx Palisade-services
     >> cd Palisade-services
     ```

1. Start the palisade services and run the example using the services manager. See the services-manager README for more info.

    ```bash
    >> java -Dspring.profiles.active=discovery -jar services-manager/target/services-manager-*-exec.jar --manager.mode=run
    >> java -Dspring.profiles.active=example -jar services-manager/target/services-manager-*-exec.jar --manager.mode=run
    ```
   
1. It will take a couple of minutes for the Spring Boot services to start up. The status of this can be checked by going to http://localhost:8083. There should be 7 services in total to register with Eureka:
    - audit-service
    - data-service
    - discovery-service
    - palisade-service
    - policy-service
    - resource-service
    - user-service
    
1. The RestExample example-model runner (in particular, with the *rest* profile from the *application-rest.yaml*) will be run immediately afterwards
    * The stdout and stderr will by default be stored in `Palisade-services/rest-example.log` and `Palisade-service/rest-example.err` respectively.
    
1. Stop the REST services. See the services-manager README for more info.
    
    ```bash
    >> java -Dspring.profiles.active=example -jar services-manager/target/services-manager-*-exec.jar --manager.mode=shutdown
    >> java -Dspring.profiles.active=discovery -jar services-manager/target/services-manager-*-exec.jar --manager.mode=shutdown
    ```
