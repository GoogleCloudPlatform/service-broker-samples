# GCP Service Broker Sample Applications [![Build Status](https://travis-ci.org/GoogleCloudPlatform/service-broker-samples.svg?branch=master)](https://travis-ci.org/GoogleCloudPlatform/service-broker-samples)  [![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This repository holds sample application which use GCP services provided by the [GCP Service Broker](https://github.com/GoogleCloudPlatform/gcp-service-broker/).

The applications are designed to be easily deployed to any platform which integrates with
the GCP Service Broker, such as [Pivotal Cloud Foundry](https://pivotal.io/platform).

This is not an officially supported Google product.

## CF on Kubernetes Demo
[![Open in Cloud Shell](https://gstatic.com/cloudssh/images/open-btn.svg)](https://console.cloud.google.com/cloudshell/editor?cloudshell_git_repo=https://github.com/GoogleCloudPlatform/service-broker-samples&cloudshell_git_branch=cf-on-k8s&cloudshell_image=gcr.io/cloud-graphite-ci/cf-in-k8s-cloudshell&cloudshell_tutorial=docs/cf-on-k8s-cloudshell-tutorial.md&cloudshell_working_dir=cf-on-k8s&cloudshell_open_in_editor=terraform.tfvars)

## Contributing changes

Entirely new samples are accepted if they showcase something new or provide a unique perspective or use-case, we'll help get them ready to be added.
Bug fixes are welcome, either as pull requests or as GitHub issues.

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute.

## Showcased brokered services

These Google Cloud Services provided by the service broker are used throughout the sample applications:

### [Google Cloud Storage](https://github.com/GoogleCloudPlatform/gcp-service-broker/blob/master/docs/use.md#-google-cloud-storage)
* awwvision 

### [Google Cloud SQL MySQL](https://github.com/GoogleCloudPlatform/gcp-service-broker/blob/master/docs/use.md#-google-cloudsql-mysql)
* link-shortener

### [Google Spanner](https://github.com/GoogleCloudPlatform/gcp-service-broker/blob/master/docs/use.md#-google-spanner)
* storelocator

### [Google Machine Learning APIs](https://github.com/GoogleCloudPlatform/gcp-service-broker/blob/master/docs/use.md#-google-cloudsql-mysql)
* awwvision

## Licensing

Code in this repository is licensed under the [Apache 2.0 License](LICENSE).
