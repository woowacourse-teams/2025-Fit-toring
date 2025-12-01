// KMS Key Policy 정의
// EC2 역할과 루트 사용자에게 Key 사용 권한을 부여한다.
data "aws_iam_policy_document" "rds_kms_key_policy" {
  statement {
    sid = "Enable Key Usage for Project IAM Role"
    effect = "Allow"

    principals {
      type = "AWS"
      identifiers = [aws_iam_role.ec2_project_role.arn]
    }

    actions = [
      "kms:Encrypt",
      "kms:Decrypt",
      "kms:ReEncrypt*",
      "kms:GenerateDataKey*",
      "kms:DescribeKey"
    ]
    resources = ["*"]
  }

  // Key 관리 권한은 계정 루트에게 부여
  statement {
    sid = "Allow Admin Key Access"
    effect = "Allow"
    principals {
      type = "AWS"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
    }
    actions = ["kms:*"]
    resources = ["*"]
  }
}

// KMS Key 생성 및 정책 적용
resource "aws_kms_key" "rds_encryption_key" {
  description = "KMS Key for Project RDS encryption"
  deletion_window_in_days = 10
  policy = data.aws_iam_policy_document.rds_kms_key_policy.json
  enable_key_rotation = true
}