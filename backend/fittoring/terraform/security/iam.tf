data "aws_caller_identity" "current" {}

// EC2 인스턴스 프로파일 역할을 위한 기본 정책
data "aws_iam_policy_document" "ec2_assume_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

// EC2 인스턴스 역할
resource "aws_iam_role" "ec2_project_role" {
  name = "ec2-project-role"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role_policy.json
}