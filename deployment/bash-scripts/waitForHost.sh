#!/bin/sh
# wait for a service to respond - allows services that are dependant upon others to wait for them

set -e

host="$1"
shift
cmd="$@"

until $(curl --output /dev/null -sf $host); do
  echo >&2 "$host is unavailable - sleeping"
  sleep 1
done

echo >&2 "$host is up - executing command"
exec $cmdw
