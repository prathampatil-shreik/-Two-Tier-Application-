# Research and Written Analysis

**Reference:** SG/PA/DO-16-L2
**Author:** Prathamesh Patil

---

## 1. What is a Two-Tier (or Three-Tier) Architecture, and Why Does the Database Belong in a Private Subnet?

A two-tier architecture separates an application into two layers: a **presentation/logic tier** (the web server or application server that users interact with) and a **data tier** (the database that stores persistent data). In a three-tier architecture, the presentation layer (UI) is further separated from the application logic layer, giving three distinct tiers.

The database belongs in a private subnet because it should never be directly reachable from the internet. The only system that needs to talk to the database is the application server. Placing the database in a private subnet — one with no route to an internet gateway — means there is no network path from the internet to the database at all. Even if an attacker discovered the database endpoint, the routing and security group rules would prevent any connection.

AWS documents this pattern in the Amazon VPC documentation:
> "A private subnet is a subnet that doesn't have a route to the internet gateway. Resources in a private subnet can't be reached directly from the internet."
> — *AWS Documentation, Amazon VPC — Subnet types* (https://docs.aws.amazon.com/vpc/latest/userguide/configure-subnets.html)

AWS also recommends this pattern for RDS:
> "We recommend that you create your DB instance in a virtual private cloud (VPC) based on Amazon VPC. A VPC is a virtual network that closely resembles a traditional network that you might operate in your own data center... For the greatest security, you should place your DB instance in a private subnet."
> — *AWS Documentation, Amazon RDS — Working with a DB instance in a VPC* (https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.WorkingWithRDSInstanceinaVPC.html)

---

## 2. The Benefit of Terraform Over Manual Infrastructure

Creating infrastructure by hand through the AWS Console is error-prone, slow, and not repeatable. If you need to recreate the environment (for a new region, a new team member, or after an accidental deletion), you have to remember every click and setting.

Terraform solves this by defining infrastructure as code:

- **Repeatability:** `terraform apply` creates the exact same infrastructure every time, in any account or region.
- **Version control:** Infrastructure changes are tracked in Git, with a full history of who changed what and when.
- **Plan before apply:** `terraform plan` shows exactly what will be created, changed, or destroyed before anything happens — reducing the risk of mistakes.
- **Destroy cleanly:** `terraform destroy` removes all managed resources, preventing forgotten resources that incur cost.

> "Infrastructure as Code (IaC) is the process of managing and provisioning computer data centers through machine-readable configuration files, rather than through interactive configuration tools."
> — *HashiCorp, What is Infrastructure as Code* (https://developer.hashicorp.com/terraform/tutorials/aws-get-started/infrastructure-as-code)

---

## 3. The Benefit of a CI/CD Pipeline Over Manual Deployment

Deploying manually means SSHing into a server, copying files, and restarting services by hand. This is slow, inconsistent, and risky — a missed step can break the application.

A CI/CD pipeline (GitHub Actions in this project) automates the entire process:

- **Consistency:** Every deployment follows the exact same steps, every time.
- **Speed:** A push to `main` triggers the build and deploy automatically — no manual steps.
- **Safety:** The pipeline runs tests before deploying. A failing test stops the deployment.
- **Auditability:** Every deployment is logged in GitHub Actions with the commit that triggered it.

> "Continuous delivery is a software development practice where code changes are automatically prepared for a release to production."
> — *AWS, What is Continuous Delivery* (https://aws.amazon.com/devops/continuous-delivery/)

---

## 4. How the Web Tier Reaches the Database While the Internet Cannot

This is controlled by two layers working together:

**Subnet routing:**
- The public subnet has a route to the internet gateway (`0.0.0.0/0 → igw`), so EC2 can send and receive internet traffic.
- The private subnets have no route to the internet gateway. Traffic from the internet has no path to reach the private subnet.

**Security groups:**
- The database security group (`two-tier-app-db-sg`) has one inbound rule: allow TCP port 3306 **from the web security group** (`two-tier-app-web-sg`). No CIDR block is allowed — only traffic originating from an instance that belongs to the web security group.
- The web security group allows inbound HTTP (80) and SSH (22) from `0.0.0.0/0`.

The result: the EC2 instance (which belongs to `two-tier-app-web-sg`) can connect to RDS on port 3306. Any other source — including the internet — is rejected by the security group rule, and also has no network route to the private subnet.

> "Security groups act as a virtual firewall for your EC2 instances to control inbound and outbound traffic... You can specify allow rules, but not deny rules."
> — *AWS Documentation, Amazon EC2 — Security groups* (https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-security-groups.html)
