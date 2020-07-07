#! /usr/bin/env bash

helm dep up
helm upgrade --install palisade .
