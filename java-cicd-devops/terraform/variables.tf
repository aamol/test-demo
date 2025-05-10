# Variables for Terraform configuration

variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment (e.g. dev, prod)"
  type        = string
  default     = "prod"
}

variable "app_name" {
  description = "Name of the application"
  type        = string
  default     = "java-cicd-demo"
}

variable "ami_id" {
  description = "AMI ID for EC2 instance"
  type        = string
  # Amazon Linux 2 AMI - update as needed
  default     = "ami-0c02fb55956c7d316"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t2.micro"
}

variable "key_name" {
  description = "SSH key name for EC2 access"
  type        = string
  default     = "app-key"
}

variable "docker_image" {
  description = "Docker image to deploy"
  type        = string
  default     = "example.com/java-cicd-demo"
}