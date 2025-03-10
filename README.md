# Order Status Notifications
This microservice:
* listens to order status updates from a Kafka topic (order-status and order-status-dlq)
* stores notifications in a PostgreSQL database
* sends email notifications to customers
* supports retrying failed notifications
* filtering by status and date
* automatic cleanup of outdated messages.

## How it works
Need to add information

## Getting started
* Requirements:

```
 - [Docker](https://www.docker.com/get-started)
 - [Docker Compose](https://docs.docker.com/compose/)
   Optional for local development:
 - Java 17
 - Apache Maven
```
* Clone the repository:

```
git clone https://github.com/MrOlegZdr/order-status-notifications.git
cd order-status-notifications
```
* Create docker.env file in order-status-notifications folder and set variables as listed below  
(in case of local development you need to set environment variables PG_USERNAME and PG_PASSWORD)

```
DATABASE_URL=jdbc:postgresql://postgres:5432/notifications
PG_USERNAME=postgres
PG_PASSWORD=yourPassword
```
## Settings
### Setting up the use of mail-service (application.yml)

* Settings for using local SMTP-server (already in use):

```
spring:
  mail:
    host: localhost
    port: 1025
    username:
    password:
    properties:
      mail.smtp.auth: false
      mail.smtp.starttls.enable: false      
```
* Settings for using Gmail SMTP. You can't use a regular password - you need an App Password (enable in your Google Account):

```
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${GMAIL_USERNAME}
    password: ${GMAIL_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true      
```
* You can use any SMTP-service you like.

### Setting up the use of Gmail SMTP (docker-compose.yml)

* Replace local SMTP-server settings (specified below):

```
app:
  environment:
    spring.mail.host: host.docker.internal
```
with Gmail SMTP settings:

```
app:
  environment:
    spring.mail.host: smtp.gmail.com
    spring.mail.port: 587
    spring.mail.username: ${GMAIL_USERNAME}
    spring.mail.password: ${GMAIL_PASSWORD}
    spring.mail.properties.mail.smtp.auth: "true"
    spring.mail.properties.mail.smtp.starttls.enable: "true"
```
* Add environment variables GMAIL_USERNAME and GMAIL_PASSWORD to the docker.env file:  

```
GMAIL_USERNAME=your-email@gmail.com
GMAIL_PASSWORD=your-app-password
```

## Starting the application
### Starting service on local computer
* Build .jar file:

```
cd order-status-notifications
mvn clean install -DskipTests
```
* Start Zookeeper and Kafka. Start local SMTP-server.
* Start application:

```
cd target
java -jar ordernotifications-0.0.1-SNAPSHOT.jar
```

### Starting service via Docker (using the project on the local computer)
* In terminal make sure you are in the application folder and build .jar file:

```
cd order-status-notifications
mvn clean install -DskipTests
```
* As a result, you will receive a file:

```
target/ordernotifications-0.0.1-SNAPSHOT.jar
```

* Build and start the service:

```
cd order-status-notifications
docker-compose up --build
```
* If you need to use clean build without cache, use next commands:

```
docker-compose build --no-cache && docker-compose up
```
* Stopping and removing containers:

```
docker-compose down
```
### Starting service via Docker (using the project on DockerHub)
* Change application settings in docker-compose.yml file:

```
app:
  build:
    context: .
    dockerfile: Dockerfile
```
with using DockerHub image:

```
app:
  image: mrolegzdr/order-status-notifications:latest
```
* Starting containers:

```
docker-compose up
```
* Stopping and removing containers:

```
docker-compose down
```

## Using the application
### Format requests (The API will be available at http://localhost:8080)

* Receive notifications with specific {orderId}:

```
GET method:
http://localhost:8080/notifications/order/{orderId}
```
* This command supports pagination:

```
GET method:
http://localhost:8080/notifications/order/{orderId}?page=0&size=10&sort=createdAt,desc
```
* Receive notifications with specific filters {status}, startDate {stD} (e.g. 2025-02-20T16:30:00), endDate {endD} (e.g. 2025-02-22T00:00:00):

```
GET method:
http://localhost:8080/notifications/filter?status={status}&startDate={stD}&endDate={endD}&page=0&size=10
```
* Resent notification with specific {notificationId}:

```
POST method:
http://localhost:8080/notifications/resend/{notificationId}
```
* Resent all unsent notifications:

```
POST method:
http://localhost:8080/notifications/resend/unsent
```

## Used technology
* Java 17
* Spring Boot 3.4.2
