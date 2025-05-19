# AI-Enhanced DevOps for Java Developers

This project demonstrates practical implementations of AI and Foundation Models (SLMs) in a Java DevOps workflow, as described in the blog post "Integrating Modern DevOps Practices for Java Developers with AI/GenAI and SLMs: A Comprehensive Guide".

## Project Overview

This demo includes:

1. A Spring Boot Java application with AI-assisted code quality checks
2. CI/CD pipeline with AI-powered testing and deployment
3. Performance monitoring with AI predictions
4. Code generation examples using SLMs
5. Automated documentation generation

## Getting Started

1. Clone this repository
2. Set up your OpenAI API key in the configuration
3. Run the Maven build: `mvn clean install`
4. Start the application: `java -jar target/ai-devops-demo-0.0.1-SNAPSHOT.jar`

## Project Structure

- `src/main/java` - Main application code
- `src/test/java` - Test code including AI-powered tests
- `.github/workflows` - CI/CD pipeline configuration
- `ai-tools` - Scripts for AI-assisted development
- `docs` - Documentation and guides

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker
- OpenAI API key
- GitHub account