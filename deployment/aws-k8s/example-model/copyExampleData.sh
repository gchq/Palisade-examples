#!/usr/bin/env bash
# Copyright 2020 Crown Copyright
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

helpFunction() {
   echo ""
   echo "Usage: $(basename $0) [OPTIONS]"
   echo -e "\t-d(atastore)     The URL for the (EFS) aws volume handle used as a data-store"
   echo -e "\t-c(lasspathjars) The URL for the (EFS) aws volume handle used for storing classpath JARs"
   exit 1 # Exit script after printing help
}

while getopts "d:c:" opt
do
   case "$opt" in
      d) datastore="$OPTARG" ;;
      c) classpathjars="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Print helpFunction in case parameters are empty
if [ -z "$datastore" ] || [ -z "$classpathjars" ]; then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

# Begin script in case all parameters are correct
# Create and copy datastore data
mkdir /mnt/datastore-efs
mount -t nfs -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2,noresvport ${datastore}:/ datastore-efs-mountpoint
cp resources/data/* /mnt/datastore-efs
umount /mnt/datastore-efs

# Create and copy classpathjars data
mkdir /mnt/classpathjars-efs
mount -t nfs -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2,noresvport ${classpathjars}:/ classpathjars-efs-mountpoint
cp deployment/target/* /mnt/classpathjars-efs
umount /mnt/classpathjars-efs
