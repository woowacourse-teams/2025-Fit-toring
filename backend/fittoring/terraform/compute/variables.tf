variable "vpc_id" {
  type = string
}

variable "public_subnet_id" {
  type = string
}

variable "public_subnet2_id" {
  type = string
}

variable "private_subnet_id" {
  type = string
}

variable "ec2_public_security_group_id" {
  type = string
}

variable "ec2_private_security_group_id" {
  type = string
}

variable "lb_public_security_group_id" {
  type = string
}

variable "fittoring_s3_id" {
  type = string
}

variable "fittoring_s3_arn" {
  type = string
}

variable "fittoring_certificate_arn" {
  type = string
}