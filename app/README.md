# Two-Tier Application

A Spring Boot web application demonstrating a two-tier architecture on AWS, with an EC2 web server connecting to an Amazon RDS MySQL database.

## Architecture

```
Internet
    |
  Port 80
    |
  Nginx (reverse proxy)
    |
  Port 8080
    |
Spring Boot Application
    |
  Port 3306
    |
Amazon RDS MySQL
```

## Technologies

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- MySQL Connector/J
- Spring Boot Actuator
- Maven

## Project Structure

```
app/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/shreik/twotierapp/
    │   │   ├── TwoTierAppApplication.java
    │   │   ├── controller/MessageController.java
    │   │   ├── service/MessageService.java
    │   │   ├── repository/MessageRepository.java
    │   │   ├── model/Message.java
    │   │   └── exception/
    │   │       ├── MessageNotFoundException.java
    │   │       └── GlobalExceptionHandler.java
    │   └── resources/
    │       ├── application.properties
    │       ├── logback-spring.xml
    │       └── static/
    │           ├── index.html
    │           ├── style.css
    │           └── app.js
    └── test/
        ├── java/com/shreik/twotierapp/
        │   ├── TwoTierAppApplicationTests.java
        │   ├── MessageServiceTest.java
        │   └── MessageControllerTest.java
        └── resources/
            └── application.properties
```

## Required Environment Variables

| Variable      | Description               | Example                  |
|---------------|---------------------------|--------------------------|
| `DB_HOST`     | RDS endpoint hostname     | `mydb.xxxx.rds.amazonaws.com` |
| `DB_PORT`     | MySQL port (default 3306) | `3306`                   |
| `DB_NAME`     | Database name             | `twotierdb`              |
| `DB_USERNAME` | Database username         | `appadmin`               |
| `DB_PASSWORD` | Database password         | `<your-password>`        |

**Never commit real credentials to Git.**

## How to Build

```bash
cd app
mvn clean package
```

The JAR will be created at `app/target/two-tier-app-1.0.0.jar`.

## How to Run Locally

Set environment variables and run:

**Linux / macOS:**
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=twotierdb
export DB_USERNAME=<your-db-username>
export DB_PASSWORD=<your-db-password>

java -jar target/two-tier-app-1.0.0.jar
```

**Windows (PowerShell):**
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="twotierdb"
$env:DB_USERNAME="<your-db-username>"
$env:DB_PASSWORD="<your-db-password>"

java -jar target\two-tier-app-1.0.0.jar
```

## How to Run Tests

Tests use an H2 in-memory database — no real credentials required.

```bash
cd app
mvn clean test
```

## API Endpoints

| Method | Endpoint              | Description              |
|--------|-----------------------|--------------------------|
| GET    | `/`                   | Web UI home page         |
| POST   | `/api/messages`       | Create a new message     |
| GET    | `/api/messages`       | Get all messages         |
| GET    | `/api/messages/{id}`  | Get message by ID        |
| GET    | `/actuator/health`    | Application health check |

## Example Requests

**POST /api/messages**
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"name": "Prathamesh", "message": "Hello from the two-tier application"}'
```

**GET /api/messages**
```bash
curl http://localhost:8080/api/messages
```

**GET /actuator/health**
```bash
curl http://localhost:8080/actuator/health
```

## Health Endpoint Response

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```
