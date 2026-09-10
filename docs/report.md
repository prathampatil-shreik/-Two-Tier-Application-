# Project Report — Two-Tier Application with IaC + CI/CD + Monitoring

**Reference:** SG/PA/DO-16-L2
**Author:** Prathamesh Patil
**Date:** September 2026

---

## 1. Architecture Overview

This project deploys a two-tier web application on AWS:

- **Web Tier:** A Spring Boot application running on an EC2 instance in a public subnet, fronted by Nginx as a reverse proxy.
- **Database Tier:** An Amazon RDS MySQL instance in a private subnet, accessible only from the web tier.

```
Internet
    |
  Port 80 (HTTP)
    |
  Nginx — EC2 t3.micro (ap-south-1a, public subnet 10.0.1.0/24)
    |
  Port 8080
    |
  Spring Boot Application (Java 17)
    |
  Port 3306 (MySQL)
    |
  Amazon RDS MySQL 8.0 — db.t3.micro (private subnets 10.0.2.0/24, 10.0.3.0/24)
```

---

## 2. How the Tiers Connect and How the Database is Kept Private

The network is built around a VPC (`10.0.0.0/16`) with three subnets:

- **Public subnet** (`10.0.1.0/24`, ap-south-1a) — EC2 web server
- **Private subnet 1** (`10.0.2.0/24`, ap-south-1a) — RDS primary
- **Private subnet 2** (`10.0.3.0/24`, ap-south-1b) — RDS standby (required by RDS subnet group)

The database is kept private through two mechanisms:

1. **Subnet placement:** RDS is placed in private subnets with no route to the internet gateway. There is no path from the internet to the database at the network level.

2. **Security groups:** The database security group (`two-tier-app-db-sg`) only allows inbound TCP on port 3306 from the web security group (`two-tier-app-web-sg`). No CIDR block from the internet is allowed. The web security group allows inbound HTTP (80) and SSH (22) from the internet.

This means even if someone knew the RDS endpoint, they could not connect to it — the security group would reject the connection because it does not originate from the web security group.

---

## 3. CI/CD Pipeline Flow

The pipeline is defined in `.github/workflows/deploy.yml` and triggers on every push to `main` that modifies files under `app/`.

**Steps:**
1. GitHub Actions runner checks out the code
2. Java 17 is set up with Maven cache
3. `mvn clean package -DskipTests` builds the executable JAR
4. The JAR is copied to EC2 via SCP using the SSH key stored in GitHub Secrets
5. SSH into EC2: write the environment file, move the JAR, restart the systemd service
6. Wait 20 seconds, then call `/actuator/health` to confirm the app is running

**Secrets used (never committed):**
- `EC2_SSH_KEY` — private key for SSH
- `EC2_HOST` — EC2 public IP
- `DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` — RDS credentials

---

## 4. Monitoring

- **CloudWatch Agent** on EC2 ships application logs to CloudWatch Logs
- **Metric Filter** on the log group matches lines containing `ERROR`
- **CloudWatch Alarm** triggers when error count exceeds threshold, sending an SNS email notification
- **CloudWatch Dashboard** shows: EC2 CPU utilisation, application error count, and RDS connections

---

## 5. Database Credentials Approach

Credentials are never stored in source code or committed to Git. The approach:

- `terraform.tfvars` (which contains RDS username/password) is listed in `.gitignore`
- The Spring Boot app reads credentials from environment variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- On EC2, credentials are written to `/etc/two-tier-app/app.env` by the pipeline, sourced by the systemd service
- In the pipeline, credentials come from GitHub Secrets

---

## 6. One Thing I Would Improve Next

The current setup stores the database password in a GitHub Secret and writes it to a file on EC2. A better approach would be to use **AWS Secrets Manager**: store the RDS credentials there, grant the EC2 instance an IAM role with permission to read the secret, and have the application fetch credentials at startup. This removes the password from the pipeline entirely and allows credential rotation without redeployment.
