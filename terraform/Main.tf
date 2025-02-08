terraform {
  backend "s3" {
    bucket = "ms-lanchonete"
    key    = "deployment-service/terraform.tfstate"
    region = "us-east-1"
  }
}

module "mslanchonete" {
  source = "./infra"

  project_name = var.projectname
  region       = var.aws_region
  appversion   = var.app_version
  bucket       = var.buckets3
  taskqueue    = var.task_queue
  processqueue = var.process_queue
  accesskey    = var.access_key
  token_secret = var.tokensecret
  cluster      = var.cluster_name
}

variable "aws_region" {
  type        = string
  default     = "us-east-1"
  description = "AWS region"
}

variable "app_version" {
  type        = string
  description = "version to deploy"
}

variable "projectname" {
  type        = string
  default     = "videoprocessor"
  description = "Application Name"
}

variable "cluster_name" {
  type        = string
  default     = "hackaton"
  description = "cluster Name"
}

variable "tokensecret" {
  type      = string
  sensitive = true
}

variable "buckets3" {
  type        = string
  description = "bucket name"
}

variable "task_queue" {
  type        = string
  description = "task queue"
}

variable "process_queue" {
  type        = string
  description = "process queue"
}

variable "access_key" {
  type        = string
  description = "process queue"
}