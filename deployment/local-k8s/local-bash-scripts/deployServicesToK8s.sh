#! /usr/bin/env bash

helm dep up
helm upgrade --install palisade . --set global.persistence.dataStores.palisade-data-store.local.hostPath=$(pwd)/resources/data --set global.persistence.classpathJars.local.hostPath=$(pwd)/example-library/target
