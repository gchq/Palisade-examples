#! /usr/bin/env bash

helm dep up
helm upgrade --install palisade . --set global.persistence.dataStores.palisade-data-store.local.hostPath=$(pwd)
