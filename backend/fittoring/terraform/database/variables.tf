variable "private_subnet_id" {
  type = string
}

variable "private_subnet2_id" {
  type = string
}

variable "prod_db_password" {
  type = string
  sensitive = true
}

variable "dev_db_password" {
  type = string
  sensitive = true
}

variable "rds_encryption_key_arn" {
  type = string
}

variable "db_security_group_id" {
  type = string
}