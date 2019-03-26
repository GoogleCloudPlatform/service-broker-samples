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

data "external" "uaa_cert" {
  depends_on = ["helm_release.uaa"]
  program = ["./scripts/get-cert.sh", "${var.cluster_name}", "${var.zone}", "${var.project_id}"]
}

resource "helm_release" "uaa" {

  name       = "uaa"
  namespace  = "uaa"
  repository = "${helm_repository.default.name}"
  chart      = "uaa"
  values = [ "${data.template_file.helm_config.rendered}" ]
  wait = false

  # wait for the load balancer ips to exist
  provisioner "local-exec" {
    command = "sleep 120"
  }
}

data "kubernetes_service" "uaa-uaa-public" {
  depends_on = ["helm_release.uaa"]

  metadata {
    name = "uaa-uaa-public"
    namespace = "uaa"
  }
}

resource "google_dns_record_set" "uaa-uaa-public" {
  name = "uaa.${google_dns_managed_zone.cf_zone.dns_name}"
  managed_zone = "${google_dns_managed_zone.cf_zone.name}"
  type = "A"
  ttl  = 30

  rrdatas = ["${data.kubernetes_service.uaa-uaa-public.load_balancer_ingress.0.ip}"]
}

resource "google_dns_record_set" "uaa-uaa-public-wildcard" {
  name = "*.uaa.${google_dns_managed_zone.cf_zone.dns_name}"
  managed_zone = "${google_dns_managed_zone.cf_zone.name}"
  type = "A"
  ttl  = 30

  rrdatas = ["${data.kubernetes_service.uaa-uaa-public.load_balancer_ingress.0.ip}"]
}

