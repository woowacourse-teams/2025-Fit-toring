resource "aws_security_group" "ec2_private_security_group" {
  name = "ec2-private-security-group"
  description = "ec2 private security group"
  vpc_id = var.vpc_id

  # 인바운드 규칙
  ingress {
    description = "self"
    from_port = 0
    to_port = 0
    protocol = "-1" # 모든 프로토콜
    self = true
  }
  ingress {
    description = "bastion"
    from_port = 22
    to_port = 22
    protocol = "tcp"
    security_groups = [aws_security_group.ec2_public_security_group.id]
  }
  ingress {
    description = "http from lb"
    from_port = 80
    to_port = 80
    protocol = "tcp"
    security_groups = [aws_security_group.lb_public_security_group.id]
  }
  ingress {
    description = "https from lb"
    from_port = 443
    to_port = 443
    protocol = "tcp"
    security_groups = [aws_security_group.lb_public_security_group.id]
  }

  # 아웃바운드 규칙
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Fittoring-EC2-Private-Security-Group"
    ProjectTeam = "fittoring"
  }
}

resource "aws_security_group" "ec2_public_security_group" {
  name        = "ec2-public-security-group"
  description = "ec2 public security group"
  vpc_id      = var.vpc_id

  ingress {
    description = "self"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    self        = true
  }
  ingress {
    description = "http"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "http2"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "https"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "smtp"
    from_port   = 25
    to_port     = 25
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Fittoring-EC2-Public-Security-Group"
    ProjectTeam = "fittoring"
  }
}

resource "aws_security_group" "lb_public_security_group" {
  name        = "lb-public-security-group"
  description = "lb public security group"
  vpc_id      = var.vpc_id

  ingress {
    description = "http"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "https"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "smtp"
    from_port   = 25
    to_port     = 25
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Fittoring-LB-Public-Security-Group"
    ProjectTeam = "fittoring"
  }
}

resource "aws_security_group" "db_security_group" {
  name = "db-security-group"
  description = "allow 3306 to application"
  vpc_id = var.vpc_id

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2_private_security_group.id]
  }
  ingress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    self        = true
  }
  ingress {
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    description     = "for MongoDB"
    security_groups = [aws_security_group.ec2_private_security_group.id]
  }
  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    description     = "bastion"
    security_groups = [aws_security_group.ec2_public_security_group.id]
  }
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    description     = "for postgresql"
    security_groups = [aws_security_group.ec2_public_security_group.id]
  }
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    description     = "for postgresql"
    security_groups = [aws_security_group.ec2_private_security_group.id]
  }
  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2_private_security_group.id]
  }
  ingress {
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    description     = "for MongoDB"
    security_groups = [aws_security_group.ec2_public_security_group.id]
  }
  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2_public_security_group.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "DB-Security-Group"
    ProjectTeam = "fittoring"
  }
}