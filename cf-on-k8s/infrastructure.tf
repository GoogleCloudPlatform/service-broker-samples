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

provider "google" {
  project = "${var.project_id}"
  region  = "${var.region}"
}

provider "acme" {
  server_url = "https://acme-staging-v02.api.letsencrypt.org/directory"
}


data "google_container_engine_versions" "default" {
  zone = "${var.zone}"
}

data "google_client_config" "current" {}

resource "google_container_cluster" "default" {
  name = "${var.cluster_name}"
  zone = "${var.zone}"
  initial_node_count = 1
  min_master_version = "${data.google_container_engine_versions.default.latest_master_version}"
  enable_legacy_abac = true

  node_config {
    image_type = "UBUNTU"
    machine_type = "n1-standard-16"
    metadata {
      "disable-legacy-endpoints" = "true"
    }
  }

  // Wait for the GCE LB controller to cleanup the resources.
  provisioner "local-exec" {
    when    = "destroy"
    command = "sleep 90"
  }
}

data "google_dns_managed_zone" "root_zone" {
  name = "${var.root_zone_name}"
}

resource "google_dns_managed_zone" "cf_zone" {
  name        = "${var.cluster_name}"
  dns_name    = "${var.scf_subdomain}.${data.google_dns_managed_zone.root_zone.dns_name}"
  description = "DNS zone for the SCF/Eirini cluster ${var.cluster_name}"
}

resource "google_dns_record_set" "ns_record" {
  managed_zone = "${data.google_dns_managed_zone.root_zone.name}"
  name = "${var.scf_subdomain}.${data.google_dns_managed_zone.root_zone.dns_name}"
  rrdatas = [
    "${google_dns_managed_zone.cf_zone.name_servers}",
  ]
  ttl = 30
  type = "NS"
}

resource "tls_private_key" "private_key" {
  algorithm = "RSA"
}

resource "acme_registration" "reg" {
  account_key_pem = "${tls_private_key.private_key.private_key_pem}"
  email_address   = "${var.email}"
}

resource "acme_certificate" "bits_cert" {
  account_key_pem = "${acme_registration.reg.account_key_pem}"

  # Strip off the dot
  common_name = "registry.${substr(google_dns_managed_zone.cf_zone.dns_name, 0, length(google_dns_managed_zone.cf_zone.dns_name) - 1)}"

  dns_challenge {
    provider = "gcloud"
    config {
      GCE_POLLING_INTERVAL="2"
      GCE_PROPAGATION_TIMEOUT="300"
      GCE_PROJECT = "${var.project_id}"
      GCE_TTL = "5"
    }
  }
}
