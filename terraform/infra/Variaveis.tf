variable "project_name" {
  type        = string
  description = "Application Name"
}

variable "region" {
  type        = string
  description = "AWS region"
}

variable "appversion" {
  type        = string
  description = "version to deploy"
}

variable "cluster" {
  type        = string
  description = "cluster eks"
}

variable "bucket" {
  type        = string
  description = "bucket name"
}

variable "taskqueue" {
  type        = string
  description = "task queue"
}

variable "processqueue" {
  type        = string
  description = "process queue"
}

variable "accesskey" {
  type        = string
  description = "process queue"
}

variable "token_secret" {
  type      = string
  sensitive = true
}