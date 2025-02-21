# Order Status Notifications

This microservice:
* listens to order status updates from a Kafka topic (order-status and order-status-dlq)
* stores notifications in a PostgreSQL database
* sends email notifications to customers
* supports retrying failed notifications
* filtering by status and date
* automatic cleanup of outdated messages.

## How it works



### Setting up the use of mail-service (application.yml)

* Settings for using local SMTP-server:

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
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      
```
* You can use any SMTP-service you like.

### Starting service
1. Start Zookeeper and Kafka.
2. Start application

### Format requests (localhost example)

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
