resource "aws_instance" "fittoring_prod" {
  ami = "ami-07436874926b926ad"
  instance_type = "t4g.small"
  key_name = "key-fittoring"

  subnet_id = var.private_subnet_id
  vpc_security_group_ids = [var.ec2_private_security_group_id]

  root_block_device {
    volume_size = 30
  }

  tags = {
    Name = "Fittoring-Prod"
    ProjectTeam = "fittoring"
  }
}

resource "aws_instance" "fittoring_dev" {
  ami = "ami-0f2da15b764fba2d8"
  instance_type = "t4g.small"
  key_name = "key-fittoring"

  subnet_id = var.private_subnet_id
  vpc_security_group_ids = [var.ec2_private_security_group_id]

  root_block_device {
    volume_size = 30
  }

  tags = {
    Name = "Fittoring-Dev"
    ProjectTeam = "fittoring"
  }
}

resource "aws_instance" "fittoring_monitoring" {
  ami = "ami-05b0ed5626a8d1788"
  instance_type = "t4g.small"
  key_name = "key-fittoring"

  subnet_id = var.public_subnet_id
  vpc_security_group_ids = [var.ec2_public_security_group_id]

  root_block_device {
    volume_size = 30
  }

  tags = {
    Name = "Fittoring-Monitoring"
    ProjectTeam = "fittoring"
  }
}

resource "aws_eip_association" "monitoring_eip_association" {
  allocation_id = aws_eip.monitoring_ec2_eip.id
  instance_id = aws_instance.fittoring_monitoring.id
}