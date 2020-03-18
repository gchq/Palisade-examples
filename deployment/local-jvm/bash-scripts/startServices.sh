#!/usr/bin/env bash


FILE=../Palisade-services/services-manager/target/services-manager-*-exec.jar || echo "Cannot find services-manager-<version>-exec.jar - have you run 'mvn install' in Palisade-services?"

# Start all the services using the service manager from Palisade-services
java -jar -Dspring.profiles.active=discovery $FILE --run
java -jar -Dspring.profiles.active=services,debug,eureka $FILE --run
