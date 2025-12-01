output "ec2_public_security_group_id" {
  value = aws_security_group.ec2_public_security_group.id
}

output "ec2_private_security_group_id" {
  value = aws_security_group.ec2_private_security_group.id
}

output "lb_public_security_group_id" {
  value = aws_security_group.lb_public_security_group.id
}

output "db_security_group_id" {
  value = aws_security_group.db_security_group.id
}

output "rds_encryption_key_arn" {
  value = aws_kms_key.rds_encryption_key.arn
}

output "fittoring_certificate_arn" {
  value = data.aws_acm_certificate.fittoring_certificate.arn
}