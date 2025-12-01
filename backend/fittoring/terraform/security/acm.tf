data "aws_acm_certificate" "fittoring_certificate" {
  domain = "api.fittoring.com"
  statuses = ["ISSUED"]
  types = ["AMAZON_ISSUED"]
}