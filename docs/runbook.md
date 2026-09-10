# Runbook — Web App Loads but Cannot Connect to the Database

**Reference:** SG/PA/DO-16-L2
**Symptom:** The web page loads but API calls fail or return 500 errors indicating a database connection failure.

---

## Steps to Check (in order)

1. **Check the app logs on EC2** — SSH in and run `sudo journalctl -u two-tier-app -n 50`. Look for `ERROR` lines mentioning connection refused, timeout, or authentication failure.

2. **Verify environment variables** — Run `sudo cat /etc/two-tier-app/app.env` and confirm `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` are set correctly and match the RDS endpoint from `terraform output`.

3. **Check the security group** — In the AWS Console, confirm the RDS security group (`two-tier-app-db-sg`) has an inbound rule allowing TCP 3306 from the web security group (`two-tier-app-web-sg`), not from a CIDR block.

4. **Test connectivity from EC2** — SSH into EC2 and run `nc -zv <DB_HOST> 3306`. If it times out, the security group or subnet routing is blocking the connection.

5. **Check RDS status** — In the AWS Console under RDS, confirm the instance status is `Available` and it is in the correct subnet group.

6. **Verify credentials** — Try connecting manually from EC2: `mysql -h <DB_HOST> -u <DB_USERNAME> -p`. An `Access denied` error means wrong credentials; a timeout means a network/security group issue.
