data "aws_ecr_repository" "repository" {
  name = "${var.project_name}-repository"
}


resource "kubernetes_deployment" "deployment" {
  depends_on = [kubernetes_secret.token_secret]
  metadata {
    name = "${var.project_name}-deployment"
  }
  spec {
    replicas = 1
    selector {
      match_labels = {
        app = var.project_name
      }
    }
    template {
      metadata {
        labels = {
          app = var.project_name
        }
      }
      spec {
        container {
          image = "${data.aws_ecr_repository.repository.repository_url}:${var.appversion}"
          name  = var.project_name
          port {
            container_port = 8080
          }
          resources {
            limits = {
              cpu    = "2"
              memory = "4096Mi"
            }
            requests = {
              cpu    = "1"
              memory = "2048Mi"
            }
          }

          env {
            name  = "AWS_REGION"
            value = var.region
          }

          env {
            name  = "BUCKET_NAME"
            value = var.bucket
          }

          env {
            name  = "TASK_QUEUE"
            value = var.taskqueue
          }

          env {
            name  = "PROCESS_QUEUE"
            value = var.processqueue
          }


          env {
            name  = "AWS_ACCESS_KEY"
            value = var.accesskey
          }

          env {
            name = "AWS_SECRET_KEY"
            value_from {
              secret_key_ref {
                name = "${var.project_name}-token-secret"
                key  = "secret"
              }
            }
          }
          # liveness_probe {
          #   http_get {
          #     path = "/"
          #     port = 8080
          #   }

          #   initial_delay_seconds = 3
          #   period_seconds        = 3
          # }
        }
      }
    }
  }
}