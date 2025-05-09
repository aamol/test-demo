# README.md

# DevOps-Enabled Java Project

This project demonstrates a modern DevOps approach for Java development, implementing the concepts discussed in the blog post "Streamlining Java Development: A Modern DevOps Approach".

## Project Structure

- **src/**: Contains the Java application code
- **test/**: Contains test cases for the application
- **.github/workflows/**: Contains GitHub Actions workflows for CI/CD
- **docker/**: Contains Dockerfile and related configurations
- **k8s/**: Contains Kubernetes deployment manifests

## Key DevOps Features

1. **CI/CD Pipeline**: Using GitHub Actions for automated build, test, and deployment
2. **Containerization**: Docker for consistent application packaging
3. **Orchestration**: Kubernetes for deployment and scaling
4. **Quality Assurance**: JUnit and SonarQube for automated testing and code quality
5. **Monitoring**: Spring Boot Actuator and Prometheus integration

## Getting Started

1. Clone the repository
2. Run `./mvnw clean install` to build the project
3. Run `docker-compose up` to start the application locally
4. Access the application at `http://localhost:8080`

## Development Workflow

The project follows a DevOps workflow:

1. Develop code on feature branches
2. Open pull requests for code review
3. Automated tests run on each PR
4. Merge to main triggers automated build and deployment
5. Monitoring and feedback loop for continuous improvement