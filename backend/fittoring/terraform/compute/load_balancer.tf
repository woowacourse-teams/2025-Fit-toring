resource "aws_lb" "fittoring_application_lb" {
  name = "fittoring-application-lb"
  internal = false
  load_balancer_type = "application"
  security_groups = [var.lb_public_security_group_id]

  subnets = [
    var.public_subnet_id,
    var.public_subnet2_id
  ]

  tags = {
    Name = "Fittoring-Application-LB"
    ProjectTeam = "fittoring"
  }
}

resource "aws_lb_listener" "http_listener" {
  load_balancer_arn = aws_lb.fittoring_application_lb.arn
  port = 80
  protocol = "HTTP"

  default_action {
    type = "fixed-response"
    fixed_response {
      content_type = "text/plain"
      message_body = "메롱 ㅋㅋ"
      status_code = "404"
    }
  }
}

resource "aws_lb_listener" "https_listener" {
  load_balancer_arn = aws_lb.fittoring_application_lb.arn
  port = 443
  protocol = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-Res-2021-06"
  certificate_arn   = var.fittoring_certificate_arn

  default_action {
    type = "fixed-response"
    fixed_response {
      content_type = "text/plain"
      message_body = "메롱 ㅋㅋ"
      status_code = "503"
    }
  }
}

resource "aws_lb_listener_rule" "http_prod_rule" {
  listener_arn = aws_lb_listener.http_listener.arn
  priority = 1

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.fittoring_prod_target_group.arn
  }
  condition {
    host_header {
      values = ["api.fittoring.com"]
    }
  }
}

resource "aws_lb_listener_rule" "http_dev_rule" {
  listener_arn = aws_lb_listener.http_listener.arn
  priority = 2

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.fittoring_dev_target_group.arn
  }
  condition {
    host_header {
      values = ["devapi.fittoring.com"]
    }
  }
}

resource "aws_lb_listener_rule" "https_prod_metrics_rule" {
  listener_arn = aws_lb_listener.https_listener.arn
  priority     = 1

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.fittoring_prod_node_exporter_target_group.arn
  }
  condition {
    host_header {
      values = ["api.fittoring.com"]
    }
  }
  condition {
    path_pattern {
      values = ["/metrics"]
    }
  }
}

resource "aws_lb_listener_rule" "https_prod_rule" {
  listener_arn = aws_lb_listener.https_listener.arn
  priority = 2

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.fittoring_prod_target_group.arn
  }
  condition {
    host_header {
      values = ["api.fittoring.com"]
    }
  }
}

resource "aws_lb_listener_rule" "https_dev_metrics_rule" {
  listener_arn = aws_lb_listener.https_listener.arn
  priority     = 3

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.fittoring_dev_node_exporter_target_group.arn
  }
  condition {
    host_header {
      values = ["devaapi.fittoring.com"]
    }
  }
  condition {
    path_pattern {
      values = ["/metrics"]
    }
  }
}

resource "aws_lb_listener_rule" "https_dev_rule" {
  listener_arn = aws_lb_listener.https_listener.arn
  priority = 4

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.fittoring_dev_target_group.arn
  }
  condition {
    host_header {
      values = ["devapi.fittoring.com"]
    }
  }
}