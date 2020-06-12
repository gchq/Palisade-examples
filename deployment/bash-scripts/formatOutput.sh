#!/usr/bin/env bash

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
