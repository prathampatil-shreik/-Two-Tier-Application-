resource "aws_key_pair" "web" {
  key_name   = "${var.project_name}-web-key"
  public_key = file("C:/Users/it/.ssh/two-tier-app-key.pem.pub")

  tags = {
    Name = "${var.project_name}-web-key"
  }
}