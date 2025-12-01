// 람다에서 AWS 서비스(로그, S3)를 사용할 수 있게 해주는 역할
resource "aws_iam_role" "image_processor_role" {
  name = "image-processor-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

// 기본 권한: 로그 남기기
resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role = aws_iam_role.image_processor_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

// 추가 권한: S3 접근 권한
resource "aws_iam_role_policy" "lambda_s3_policy" {
  name = "fittoring-s3-access"
  role = aws_iam_role.image_processor_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:ListBucket"
        ]
        Resource = [
          var.fittoring_s3_arn,
          "${var.fittoring_s3_arn}/*"
        ]
      }
    ]
  })
}

resource "aws_lambda_function" "image_processor" {
  function_name = "image_processor"
  filename = "image-processor.zip"
  source_code_hash = filebase64sha256("image-processor.zip")

  role = aws_iam_role.image_processor_role.arn
  runtime = "nodejs22.x"
  handler = "index.handler"
  timeout = 30
  memory_size = 512
  architectures = ["x86_64"]
}

resource "aws_lambda_permission" "allow_s3_trigger" {
  statement_id = "AllowExecutionFromS3"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.image_processor.function_name
  principal = "s3.amazonaws.com"

  source_arn = var.fittoring_s3_arn
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = var.fittoring_s3_id
  lambda_function {
    lambda_function_arn = aws_lambda_function.image_processor.arn
    events = ["s3:ObjectCreated:*"]
    filter_prefix = "fit-toring/dev/certificate-image/default/"
  }
  lambda_function {
    lambda_function_arn = aws_lambda_function.image_processor.arn
    events = ["s3:ObjectCreated:*"]
    filter_prefix = "fit-toring/dev/profile-image/default/"
  }
  lambda_function {
    lambda_function_arn = aws_lambda_function.image_processor.arn
    events = ["s3:ObjectCreated:Put", "s3:ObjectCreated:CompleteMultipartUpload"]
    filter_prefix = "fit-toring/dev/chat-image/"
  }
  lambda_function {
    lambda_function_arn = aws_lambda_function.image_processor.arn
    events = ["s3:ObjectCreated:*"]
    filter_prefix = "fit-toring/prod/certificate-image/default/"
  }
  lambda_function {
    lambda_function_arn = aws_lambda_function.image_processor.arn
    events = ["s3:ObjectCreated:*"]
    filter_prefix = "fit-toring/prod/profile-image/default/"
  }

  depends_on = [aws_lambda_permission.allow_s3_trigger]
}