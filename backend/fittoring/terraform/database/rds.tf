resource "aws_db_subnet_group" "rds_subnet_group" {
  name = "rds-subnet-group"
  description = "Fittoring RDS Subnet Group for multi-AZ deployment"

  subnet_ids = [var.private_subnet_id, var.private_subnet2_id]
  tags = {
    Name = "RDS_Subnet_Group"
    ProjectTeam = "fittoring"
  }
}

resource "aws_db_instance" "fittoring_prod" {
  identifier = "fittoring-prod"
  engine = "mysql"
  engine_version = "8.0.42"
  instance_class = "db.t4g.micro"
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name

  skip_final_snapshot = true

  db_name = "fittoring"
  username = "dbadmin"
  password = var.prod_db_password
  publicly_accessible = false

  allocated_storage = 20
  storage_type = "gp2"
  storage_encrypted = true
  kms_key_id = var.rds_encryption_key_arn

  multi_az = false
  vpc_security_group_ids = [var.db_security_group_id]
}

resource "aws_db_instance" "fittoring_dev_primary" {
  identifier = "fittoring-dev-primary"
  engine = "mysql"
  engine_version = "8.0.42"
  instance_class = "db.t4g.micro"
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name

  skip_final_snapshot = true

  username = "goodshot"
  password = var.dev_db_password
  publicly_accessible = false

  allocated_storage = 40
  storage_type = "gp3"
  storage_encrypted = true
  kms_key_id = var.rds_encryption_key_arn
  backup_retention_period = 1 # 프리티어는 최대 백업 기간이 1일

  multi_az = true
  vpc_security_group_ids = [var.db_security_group_id]
  deletion_protection = true
}

resource "aws_db_instance" "fittoring_dev_reader" {
  identifier = "fittoring-dev-reader"
  instance_class = "db.t4g.micro"
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name

  skip_final_snapshot = true

  replicate_source_db = aws_db_instance.fittoring_dev_primary.arn
  publicly_accessible = false

  storage_type = "gp3"
  storage_encrypted = true
  kms_key_id = var.rds_encryption_key_arn

  vpc_security_group_ids = [var.db_security_group_id]
}