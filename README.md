# video-processor

Aplicação que le uma fila sqs, busca um video no s3, extrai farmes e grava num zip no s3, e posta uma mensagem após a conclusão das etapas anteriores.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mssistemalanchonete_video-processor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mssistemalanchonete_video-processor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mssistemalanchonete_video-processor&metric=coverage)](https://sonarcloud.io/summary/new_code?id=mssistemalanchonete_video-processor)

## Como executar
Forneas seguintes variaveis de ambiente
```
AWS_REGION
AWS_ACCESS_KEY
AWS_SECRET_KEY
BUCKET_NAME
TASK_QUEUE
PROCESS_QUEUE
```
Então execute a aplicação usando maven.