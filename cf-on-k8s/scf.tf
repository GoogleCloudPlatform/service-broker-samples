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

resource "helm_release" "scf" {
  depends_on = ["data.external.uaa_cert"]
  name       = "scf"
  namespace  = "scf"
  repository = "${helm_repository.default.name}"
  chart      = "cf"
  values = [ "${data.template_file.helm_config.rendered}" ]
  set {
    name = "secrets.UAA_CA_CERT"
    value = "${data.kubernetes_secret.uaa_secrets.data["internal-ca-cert"]}"
  }
  set {
    name = "eirini.secrets.BITS_TLS_CRT"
    value = <<EOF
${acme_certificate.bits_cert.certificate_pem}
EOF
  }
  set {
    name = "eirini.secrets.BITS_TLS_KEY"
    value = <<EOF
${acme_certificate.bits_cert.private_key_pem}
EOF
  }
  wait = false

  # wait for the load balancer ips to exist
  provisioner "local-exec" {
    command = "sleep 200"
  }
}

data "kubernetes_service" "router-gorouter-public" {
  depends_on = ["helm_release.scf"]

  metadata {
    name = "router-gorouter-public"
    namespace = "scf"
  }
}


data "kubernetes_service" "tcp-router-tcp-router-public" {
  depends_on = ["helm_release.scf"]

  metadata {
    name = "tcp-router-tcp-router-public"
    namespace = "scf"
  }
}

data "kubernetes_service" "bits" {
  depends_on = ["helm_release.scf"]

  metadata {
    name = "bits"
    namespace = "scf"
  }
}

resource "google_dns_record_set" "router-gorouter-public" {
  name = "*.${google_dns_managed_zone.cf_zone.dns_name}"
  managed_zone = "${google_dns_managed_zone.cf_zone.name}"
  type = "A"
  ttl  = 30

  rrdatas = ["${data.kubernetes_service.router-gorouter-public.load_balancer_ingress.0.ip}"]
}

resource "google_dns_record_set" "tcp-router-tcp-router-public" {
  name = "tcp.${google_dns_managed_zone.cf_zone.dns_name}"
  managed_zone = "${google_dns_managed_zone.cf_zone.name}"
  type = "A"
  ttl  = 30

  rrdatas = ["${data.kubernetes_service.tcp-router-tcp-router-public.load_balancer_ingress.0.ip}"]
}

resource "google_dns_record_set" "bits" {
  name = "registry.${google_dns_managed_zone.cf_zone.dns_name}"
  managed_zone = "${google_dns_managed_zone.cf_zone.name}"
  type = "A"
  ttl  = 30

  rrdatas = ["${data.kubernetes_service.bits.load_balancer_ingress.0.ip}"]
}

resource "null_resource" "wait-for-scf" {

  triggers {
    t = "values ${join(",", helm_release.scf.values)}"
    t2 = "version ${helm_release.scf.version}"
  }

  provisioner "local-exec" {
    command = "./scripts/wait-for-scf.sh"
  }
}
