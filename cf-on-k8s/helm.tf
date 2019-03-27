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

provider "kubernetes" {
  host                   = "${google_container_cluster.default.endpoint}"
  token                  = "${data.google_client_config.current.access_token}"
  client_certificate     = "${base64decode(google_container_cluster.default.master_auth.0.client_certificate)}"
  client_key             = "${base64decode(google_container_cluster.default.master_auth.0.client_key)}"
  cluster_ca_certificate = "${base64decode(google_container_cluster.default.master_auth.0.cluster_ca_certificate)}"
}

resource "kubernetes_service_account" "helm" {
  metadata {
    name = "helm"
    namespace = "kube-system"
  }
}

resource "kubernetes_cluster_role_binding" "helm" {
  metadata {
    name = "${kubernetes_service_account.helm.metadata.0.name}"
  }
  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind = "ClusterRole"
    name = "cluster-admin"
  }
  subject {
    kind = "ServiceAccount"
    name = "${kubernetes_service_account.helm.metadata.0.name}"
    namespace = "kube-system"

    # https://github.com/terraform-providers/terraform-provider-kubernetes/issues/204
    api_group = ""
  }
}

provider "helm" {
  install_tiller = true
  tiller_image = "gcr.io/kubernetes-helm/tiller:${var.helm_version}"
  service_account = "${kubernetes_cluster_role_binding.helm.metadata.0.name}"

  kubernetes {
    host                   = "${google_container_cluster.default.endpoint}"
    token                  = "${data.google_client_config.current.access_token}"
    client_certificate     = "${base64decode(google_container_cluster.default.master_auth.0.client_certificate)}"
    client_key             = "${base64decode(google_container_cluster.default.master_auth.0.client_key)}"
    cluster_ca_certificate = "${base64decode(google_container_cluster.default.master_auth.0.cluster_ca_certificate)}"
  }
}

resource "random_string" "CLUSTER_ADMIN_PASSWORD" {
  length = 24
  special = false
}

resource "random_string" "UAA_ADMIN_CLIENT_SECRET" {
  length = 24
  special = false
}

resource "random_string" "BLOBSTORE_PASSWORD" {
  length = 24
  special = false
}

resource "random_string" "BITS_SERVICE_SECRET" {
  length = 24
  special = false
}

resource "random_string" "BITS_SERVICE_SIGNING_USER_PASSWORD" {
  length = 24
  special = false
}

data "template_file" "helm_config" {
  template = "${file("${path.module}/config-template.yml")}"
  vars = {
    DOMAIN = "${substr("${var.scf_subdomain}.${data.google_dns_managed_zone.root_zone.dns_name}", 0, length("${var.scf_subdomain}.${data.google_dns_managed_zone.root_zone.dns_name}") - 1)}"
    CLUSTER_ADMIN_PASSWORD = "${random_string.CLUSTER_ADMIN_PASSWORD.result}"
    UAA_ADMIN_CLIENT_SECRET = "${random_string.UAA_ADMIN_CLIENT_SECRET.result}"
    BLOBSTORE_PASSWORD = "${random_string.BLOBSTORE_PASSWORD.result}"
    BITS_SERVICE_SECRET = "${random_string.BITS_SERVICE_SECRET.result}"
    BITS_SERVICE_SIGNING_USER_PASSWORD = "${random_string.BITS_SERVICE_SIGNING_USER_PASSWORD.result}"
  }
}

resource "helm_repository" "default" {
  name = "eirini"
  url = "https://storage.googleapis.com/eirini/helm"
}
