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

CLUSTER_NAME=$1
ZONE=$2
PROJECT=$3

gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE} --project ${PROJECT}

set -x

max=10
count=0
kubectl_timeout=10s

CA_CERT=""
while true; do

  SECRET=$(kubectl --request-timeout=${kubectl_timeout} get pods --namespace uaa -o jsonpath='{.items[?(.metadata.name=="uaa-0")].spec.containers[?(.name=="uaa")].env[?(.name=="INTERNAL_CA_CERT")].valueFrom.secretKeyRef.name}')
  if [[ "${SECRET}" != "" ]]; then
    CA_CERT="$(kubectl --request-timeout=${kubectl_timeout} get secret ${SECRET} --namespace uaa -o jsonpath="{.data['internal-ca-cert']}" | base64 --decode -)"
  fi

  [[ "${CA_CERT}" != "" ]] && break

  count=$((count + 1))
  [[ ${count} -ge 10 ]] && exit 1

  echo sleeping... >&2 && sleep 10

done

echo "{\"ca_cert\":\"$(echo ${CA_CERT@Q} | cut -c 2- | sed "s/'//g")\"}"
