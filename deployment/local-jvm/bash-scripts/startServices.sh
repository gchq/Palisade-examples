#!/usr/bin/env bash
cd ../Palisade-services/

FILE=services-manager/target/services-manager-*-exec.jar

# Start all the services using the service manager from Palisade-services
[ -f $FILE ] && {
  java -jar -Dspring.profiles.active=discovery $FILE --run
  java -jar -Dspring.profiles.active=services,debug,eureka $FILE --run
} || echo "Cannot find services-manager-<version>-exec.jar - have you run 'mvn install' in Palisade-services?"
