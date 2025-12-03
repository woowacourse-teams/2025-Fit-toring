resource "aws_subnet" "public_subnet" {
  vpc_id = aws_vpc.vpc.id
  cidr_block = "10.0.0.0/24"
  availability_zone = "ap-northeast-2a"
  map_public_ip_on_launch = false
  tags = {
    Name = "Fittoring-Public-Subnet"
    ProjectTeam = "fittoring"
  }
}

resource "aws_subnet" "public_subnet2" {
  vpc_id = aws_vpc.vpc.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "ap-northeast-2b"
  map_public_ip_on_launch = false
  tags = {
    Name = "Fittoring-Public-Subnet2"
    ProjectTeam = "fittoring"
  }
}

resource "aws_subnet" "private_subnet" {
  vpc_id = aws_vpc.vpc.id
  cidr_block = "10.0.20.0/24"
  availability_zone = "ap-northeast-2a"
  map_public_ip_on_launch = false
  tags = {
    Name = "Fittoring-Private-Subnet"
    ProjectTeam = "fittoring"
  }
}

resource "aws_subnet" "private_subnet2" {
  vpc_id = aws_vpc.vpc.id
  cidr_block = "10.0.21.0/24"
  availability_zone = "ap-northeast-2b"
  map_public_ip_on_launch = false
  tags = {
    Name = "Fittoring-Private-Subnet2"
    ProjectTeam = "fittoring"
  }
}

resource "aws_route_table_association" "public_rt_association" {
  subnet_id = aws_subnet.public_subnet.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "private_rt_association" {
  subnet_id = aws_subnet.private_subnet.id
  route_table_id = aws_route_table.private_route_table.id
}