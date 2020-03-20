#!/usr/bin/env bash

pgrep java | awk '{system("kill "$1)}'