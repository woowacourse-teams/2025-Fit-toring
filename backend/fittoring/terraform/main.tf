variable "prod_db_password" {
  type      = string
  sensitive = true
}

variable "dev_db_password" {
  type      = string
  sensitive = true
}

module "storage" {
  source = "./storage"
}

module "network" {
  source = "./network"
}

module "security" {
  source = "./security"
  vpc_id = module.network.vpc_id
}

module "database" {
  source               = "./database"
  private_subnet_id    = module.network.private_subnet_id
  private_subnet2_id   = module.network.private_subnet2_id
  db_security_group_id = module.security.db_security_group_id
  rds_encryption_key_arn = module.security.rds_encryption_key_arn
  prod_db_password     = var.prod_db_password
  dev_db_password      = var.dev_db_password
}

module "compute" {
  source                          = "./compute"
  vpc_id                          = module.network.vpc_id
  public_subnet_id                = module.network.public_subnet_id
  public_subnet2_id               = module.network.public_subnet2_id
  private_subnet_id               = module.network.private_subnet_id
  ec2_public_security_group_id    = module.security.ec2_public_security_group_id
  ec2_private_security_group_id   = module.security.ec2_private_security_group_id
  lb_public_security_group_id     = module.security.lb_public_security_group_id
  fittoring_s3_id                 = module.storage.fittoring_s3_id
  fittoring_s3_arn                = module.storage.fittoring_s3_arn
  fittoring_certificate_arn       = module.security.fittoring_certificate_arn
}
