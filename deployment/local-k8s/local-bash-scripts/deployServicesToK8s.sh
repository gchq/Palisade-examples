#! /usr/bin/env bash

helm dep up
helm upgrade --install palisade . --set global.persistence.dataStore.palisade-data-store.local.hostPath=$(pwd)
