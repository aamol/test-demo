# Use a multi-stage build for smaller final image

# Build stage
FROM maven:3.8.6-openjdk-11 as build

# Set the working directory
WORKDIR /app

# Copy the Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build and package the application
# Skip tests since they've already been run in the CI pipeline
RUN mvn package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim

# Add metadata to the image
LABEL maintainer="developer@example.com"
LABEL version="1.0"
LABEL description="Java CI/CD Demo Application"

# Create a non-root user to run the application
RUN useradd -m appuser

# Set the working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the port the application runs on
EXPOSE 8080

# Set Java options for containers
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 CMD wget -qO- http://localhost:8080/health || exit 1