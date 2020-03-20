#!/usr/bin/env bash

pgrep user-service | awk '{system("kill "$1)}'