#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

#Start all the services using the service manager from Palisade-services
(cd ../Palisade-services && java -jar -Dspring.profiles.active=jvm services-manager/target/services-manager-*-exec.jar)
