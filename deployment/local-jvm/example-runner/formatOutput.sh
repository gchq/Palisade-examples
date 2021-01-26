#!/usr/bin/env bash
# Copyright 2018-2021 Crown Copyright
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

sed $'s/, name=/\\\nname=/' | \
sed $'s/, dateOfBirth=/\\\ndateOfBirth=/' | \
sed $'s/, contactNumbers=/\\\ncontactNumbers=/' | \
sed $'s/, emergencyContacts=/\\\nemergencyContacts=/' | \
sed $'s/, address=/\\\naddress=/' | \
sed $'s/, bankDetails=/\\\nbankDetails=/' | \
sed $'s/, taxCode=/\\\ntaxCode=/' | \
sed $'s/, nationality=/\\\nnationality=/' | \
sed $'s/, manager=/\\\nmanager=/' | \
sed $'s/, hireDate=/\\\nhireDate=/' | \
sed $'s/, grade=/\\\ngrade=/' | \
sed $'s/, department=/\\\ndepartment=/' | \
sed $'s/, salaryAmount=/\\\nsalaryAmount=/' | \
sed $'s/, salaryBonus=/\\\nsalaryBonus=/' | \
sed $'s/, workLocation=/\\\nworkLocation=/' | \
sed $'s/, sex=/\\\nsex=/'
