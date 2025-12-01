// S3 버킷
resource "aws_s3_bucket" "fittoring_s3" {
  bucket = "fittoring-s3"
  force_destroy = false # TODO: 테스트 후 true로 변경
  tags = {
    Name = "Fittoring_S3"
    ProjectTeam = "fittoring"
  }
}

// S3 버킷 버전 관리 활성화
resource "aws_s3_bucket_versioning" "fittoring_s3_versioning" {
  bucket = aws_s3_bucket.fittoring_s3.id
  versioning_configuration {
    status = "Enabled"
  }
}

// S3 퍼블릭 액세스 허용 (정적 웹 사이트 호스팅을 위해)
resource "aws_s3_bucket_public_access_block" "fittoring_s3_public_access" {
  bucket = aws_s3_bucket.fittoring_s3.id

  block_public_acls = false
  block_public_policy = false
  ignore_public_acls = false
  restrict_public_buckets = false
}

// S3 버킷 정책 (Public Read 허용)
resource "aws_s3_bucket_policy" "fittoring_s3_policy" {
  bucket = aws_s3_bucket.fittoring_s3.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "PublicReadGetObject"
      Effect    = "Allow"
      Principal = "*"
      Action    = "s3:GetObject"
      Resource  = "${aws_s3_bucket.fittoring_s3.arn}/*"
    }]
  })
  depends_on = [aws_s3_bucket_public_access_block.fittoring_s3_public_access]
}

// CORS 설정 (웹 프론트엔드 접근용)
resource "aws_s3_bucket_cors_configuration" "fittoring_s3_cors" {
  bucket = aws_s3_bucket.fittoring_s3.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "PUT", "POST", "HEAD"]
    allowed_origins = ["https://fittoring.com"," https://dev.fittoring.com", "http://localhost:3000"]
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}

// 수명 주기 규칙
resource "aws_s3_bucket_lifecycle_configuration" "fittoring_s3_lifecycle" {
  bucket = aws_s3_bucket.fittoring_s3.id

  rule { // 7일 지난 불완전한 업로드 삭제
    id = "delete-incomplete-multipart-uploads"
    status = "Enabled"
    filter {}
    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }
  }

  rule { // 지능형 계층화 전환
    id = "transition-intelligent-tiering"
    status = "Enabled"
    filter {}

    transition {
      days = 0
      storage_class = "INTELLIGENT_TIERING"
    }
  }
}