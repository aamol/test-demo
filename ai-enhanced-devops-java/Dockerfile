FROM openjdk:17-slim as builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Use a smaller runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built JAR file
COPY --from=builder /app/target/*.jar app.jar

# Install AI monitoring agent
COPY ai-tools/monitoring-agent ./monitoring-agent
RUN apt-get update && apt-get install -y python3 python3-pip && \
    pip3 install -r monitoring-agent/requirements.txt && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Configuration
COPY application.properties .
COPY ai-tools/monitoring-agent/config.yml ./monitoring-agent/

# Set up healthcheck using AI-enhanced monitoring
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD python3 monitoring-agent/healthcheck.py || exit 1

# Environment configuration
ENV JAVA_OPTS="-Xms512m -Xmx1g"
ENV AI_MONITORING_ENABLED="true"

# Start the application with AI monitoring
ENTRYPOINT ["sh", "-c", "python3 monitoring-agent/start.py & java ${JAVA_OPTS} -jar app.jar"]