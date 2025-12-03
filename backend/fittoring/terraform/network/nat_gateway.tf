resource "aws_nat_gateway" "nat_gw" {
  subnet_id = aws_subnet.public_subnet.id
  allocation_id = aws_eip.nat_eip.id

  tags = {
    Name = "Fittoring-NAT-GW"
    ProjectTeam = "fittoring"
  }

  depends_on = [
    aws_internet_gateway.igw
  ]
}