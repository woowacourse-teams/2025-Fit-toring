resource "aws_lb_target_group" "fittoring_prod_target_group" {
  name = "fittoring-prod-target-group"
  port = 80
  protocol = "HTTP"
  vpc_id = var.vpc_id

  health_check {
    path = "/healthcheck"
    port = "traffic-port" # 80
    protocol = "HTTP"
    healthy_threshold = 5
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    matcher = "200"
  }
}

resource "aws_lb_target_group" "fittoring_dev_target_group" {
  name = "fittoring-dev-target-group"
  port = 80
  protocol = "HTTP"
  vpc_id = var.vpc_id

  health_check {
    path = "/healthcheck"
    port = "traffic-port" # 80
    protocol = "HTTP"
    healthy_threshold = 5
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    matcher = "200"
  }
}

resource "aws_lb_target_group" "fittoring_prod_node_exporter_target_group" {
  name = "fittoring-prod-node-exporter-tg"
  port = 80
  protocol = "HTTP"
  vpc_id = var.vpc_id

  health_check {
    path = "/metrics"
    port = "443"
    protocol = "HTTP"
    healthy_threshold = 5
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    matcher = "200"
  }
}

resource "aws_lb_target_group" "fittoring_dev_node_exporter_target_group" {
  name = "fittoring-dev-node-exporter-tg"
  port = 80
  protocol = "HTTP"
  vpc_id = var.vpc_id

  health_check {
    path = "/metrics"
    port = "443"
    protocol = "HTTP"
    healthy_threshold = 5
    unhealthy_threshold = 2
    timeout = 5
    interval = 30
    matcher = "200"
  }
}

resource "aws_lb_target_group_attachment" "fittoring_prod_attachment" {
  target_group_arn = aws_lb_target_group.fittoring_prod_target_group.arn
  target_id = aws_instance.fittoring_prod.id
  port = 80
}

resource "aws_lb_target_group_attachment" "fittoring_dev_attachment" {
  target_group_arn = aws_lb_target_group.fittoring_dev_target_group.arn
  target_id = aws_instance.fittoring_dev.id
  port = 80
}

resource "aws_lb_target_group_attachment" "fittoring_prod_node_exporter_attachment" {
  target_group_arn = aws_lb_target_group.fittoring_prod_node_exporter_target_group.arn
  target_id = aws_instance.fittoring_prod.id
  port = 443
}

resource "aws_lb_target_group_attachment" "fittoring_dev_node_exporter_attachment" {
  target_group_arn = aws_lb_target_group.fittoring_dev_node_exporter_target_group.arn
  target_id = aws_instance.fittoring_dev.id
  port = 443
}
