# Multi-stage build for optimized Java container
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (for better layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the application
COPY src/ /app/src/
RUN mvn package -DskipTests

# Use a lightweight JRE image for the final container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Create directories for logs and data
RUN mkdir -p /app/logs /app/data
VOLUME ["/app/logs", "/app/data"]

# Expose application and actuator ports
EXPOSE 8080

# Set JVM options for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1