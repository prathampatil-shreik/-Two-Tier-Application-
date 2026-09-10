# Two-Tier Application — IaC + CI/CD + Monitoring

**Project:** SG/PA/DO-16-L2 — DevOps Engineer Level 2
**Author:** Prathamesh Patil

A two-tier web application deployed on AWS with infrastructure defined in Terraform, deployed via a GitHub Actions CI/CD pipeline, and monitored with CloudWatch.

## Architecture

```
Internet
    |
  Port 80
    |
  Nginx (reverse proxy) — EC2 (public subnet)
    |
  Port 8080
    |
  Spring Boot Application
    |
  Port 3306
    |
  Amazon RDS MySQL (private subnet)
```

## Repository Structure

```
two-tier-app/
├── README.md                        # This file
├── .gitignore
├── infra/                           # Terraform — VPC, subnets, SGs, EC2, RDS
│   ├── vpc.tf
│   ├── subnet.tf
│   ├── routes.tf
│   ├── security.tf
│   ├── ec2.tf
│   ├── keypair.tf
│   ├── rds.tf
│   ├── output.tf
│   ├── provider.tf
│   └── variable.tf
├── app/                             # Spring Boot application
│   ├── pom.xml
│   ├── README.md
│   └── src/
├── .github/
│   └── workflows/
│       └── deploy.yml               # CI/CD pipeline
├── monitoring/                      # CloudWatch metric filters, alarms, dashboard
├── docs/
│   ├── report.md
│   ├── research.md
│   ├── runbook.md
│   └── screenshots/
└── screenshots/
```

## How the Tiers Connect

- The EC2 instance (web tier) lives in the **public subnet** and is reachable from the internet on port 80 via Nginx.
- The RDS MySQL instance (database tier) lives in the **private subnet** with no public access.
- The database security group only allows inbound traffic on port 3306 **from the web security group** — not from the internet.
- The Spring Boot app connects to RDS using environment variables — no credentials are stored in code.

## How to Deploy

### Prerequisites
- AWS CLI configured
- Terraform installed
- Java 17 + Maven installed

### 1. Create Infrastructure
```bash
cd infra
terraform init
terraform plan
terraform apply
```

### 2. Add GitHub Secrets
In your GitHub repository settings, add:

| Secret | Description |
|--------|-------------|
| `EC2_HOST` | EC2 public IP from Terraform output |
| `EC2_SSH_KEY` | Contents of `two-tier-app-key.pem` |
| `DB_HOST` | RDS endpoint from Terraform output |
| `DB_NAME` | `twotierdb` |
| `DB_USERNAME` | RDS username |
| `DB_PASSWORD` | RDS password |

### 3. Deploy via Pipeline
Push any change to `app/` on the `main` branch — the pipeline builds and deploys automatically.

### 4. Tear Down
```bash
cd infra
terraform destroy
```

## CI/CD Pipeline

On every push to `main` that changes files under `app/`:
1. Checkout code
2. Build JAR with Maven
3. SCP JAR to EC2
4. Restart systemd service on EC2
5. Verify health endpoint

## Monitoring

- CloudWatch agent ships application logs from EC2
- Metric filter on `ERROR` keyword in logs
- CloudWatch Alarm triggers SNS email notification
- CloudWatch Dashboard shows CPU, requests, and errors

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Web UI |
| POST | `/api/messages` | Create message |
| GET | `/api/messages` | Get all messages |
| GET | `/api/messages/{id}` | Get message by ID |
| GET | `/actuator/health` | Health check |

## Security

- Database credentials passed via environment variables only
- SSH key never committed (in `.gitignore`)
- `terraform.tfvars` never committed (in `.gitignore`)
- Pipeline secrets stored in GitHub Secrets
