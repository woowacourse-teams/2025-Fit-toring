data "aws_caller_identity" "current" {}

resource "aws_sqs_queue" "image_dlq" {
  name = "fittoring-image-dlq"
}

resource "aws_sqs_queue" "prod_image_dlq" {
  name = "fittoring-prod-image-dlq"
}

resource "aws_sqs_queue" "image_queue" {
  name = "fittoring-image-queue"
  redrive_policy = jsonencode({
    deadLetterTargetArn : aws_sqs_queue.image_dlq.arn,
    maxReceiveCount : 5
  })
}

resource "aws_sqs_queue" "prod_image_queue" {
  name = "fittoring-prod-image-queue"
  redrive_policy = jsonencode({
    deadLetterTargetArn : aws_sqs_queue.prod_image_dlq.arn,
    maxReceiveCount : 5
  })
}

resource "aws_sqs_queue_policy" "image_queue_access" {
  queue_url = aws_sqs_queue.image_queue.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid = "OwnerAccess",
        Effect = "Allow",
        Principal = {
          AWS = [
            "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root",
            aws_iam_role.image_processor_role.arn
          ]
        },
        Action = "SQS:*",
        Resource = aws_sqs_queue.image_queue.arn
      }]
  })
}

resource "aws_sqs_queue_redrive_allow_policy" "dev_dlq_redrive_allow" {
  queue_url = aws_sqs_queue.image_dlq.id
  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue",
    sourceQueueArns = [aws_sqs_queue.image_queue.arn]
  })
}

resource "aws_sqs_queue_redrive_allow_policy" "prod_dlq_redrive_allow" {
  queue_url = aws_sqs_queue.prod_image_dlq.id
  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue",
    sourceQueueArns = [aws_sqs_queue.prod_image_queue.arn]
  })
}