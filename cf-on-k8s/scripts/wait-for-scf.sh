#!/usr/bin/env bash

# Copyright 2019 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

name=${2}

# 10 min max wait time
sleep_seconds=30
attempts=20

expected_running=20
expected_completed=3

last_running=0
last_completed=0

command="[ "$(kubectl -n scf get pods | grep Running | wc -l)" = "20"  ] && [ "$(kubectl -n scf get pods | grep Completed | wc -l)" = "3"  ]"

c=0

while true; do

  running=$(kubectl -n scf get pods | grep Running | wc -l)
  completed=$(kubectl -n scf get pods | grep Completed | wc -l)

  if [ ${running} -ne ${last_running} ] || [ ${completed} -ne ${last_completed} ]; then
    echo Running: ${running}/${expected_running} Completed: ${completed}/${expected_completed}
    last_running=${running}
    last_completed=${completed}
  fi

  if [ ${running} -eq ${expected_running} ] && [ ${completed} -eq ${expected_completed} ]; then
    break
  fi

  c=$((c + 1))
  if [[ ${c} -gt ${attempts} ]]; then
    echo timeout exceeded
    exit -1
  fi

  sleep ${sleep_seconds}
done
