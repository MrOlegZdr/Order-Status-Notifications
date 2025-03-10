# Use official OpenJDK as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built application JAR
COPY target/ordernotifications-0.0.1-SNAPSHOT.jar /app/onservice.jar
COPY README.md /README.md

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "onservice.jar"]