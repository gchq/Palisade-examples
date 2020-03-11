#!/usr/bin/env bash

# Start all the services using the service manager from Palisade-services
(cd ../Palisade-services/services-manager/target && java -jar -Dspring.profiles.active=discovery services-manager-*-exec.jar --run && java -jar -Dspring.profiles.active=services,debug services-manager-*-exec.jar --run) || echo "Error running services-manager-<version>-exec.jar - have you run 'mvn install'?"
