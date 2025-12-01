resource "aws_eip" "monitoring_ec2_eip" {
  tags = {
    Name = "Fittoring-Monitoring-EC2-EIP"
    ProjectTeam = "fittoring"
  }
}