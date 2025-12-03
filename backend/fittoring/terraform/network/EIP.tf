resource "aws_eip" "nat_eip" {
  tags = {
    Name = "Fittoring-NAT-EIP"
    ProjectTeam = "fittoring"
  }
}