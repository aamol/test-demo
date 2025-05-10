#!/bin/bash
# Script to deploy the application to production

# Exit immediately if a command exits with a non-zero status
set -e

# Display commands as they are executed
set -x

# Variables
APP_NAME="java-cicd-demo"
IMAGE_NAME="example.com/$APP_NAME"
IMAGE_TAG="latest"
CONTAINER_NAME="$APP_NAME"
HOST_PORT=8080
CONTAINER_PORT=8080

# Pull the latest image
echo "Pulling latest image: $IMAGE_NAME:$IMAGE_TAG"
docker pull "$IMAGE_NAME:$IMAGE_TAG"

# Stop and remove existing container if it exists
echo "Stopping and removing existing container if it exists"
docker stop "$CONTAINER_NAME" 2>/dev/null || true
docker rm "$CONTAINER_NAME" 2>/dev/null || true

# Run the new container
echo "Starting new container"
docker run -d \
  --name "$CONTAINER_NAME" \
  -p "$HOST_PORT:$CONTAINER_PORT" \
  --restart always \
  --health-cmd="wget -qO- http://localhost:$CONTAINER_PORT/health || exit 1" \
  --health-interval=30s \
  --health-timeout=3s \
  --health-retries=3 \
  --health-start-period=60s \
  "$IMAGE_NAME:$IMAGE_TAG"

# Check if container is running
echo "Checking if container is running"
if [ "$(docker inspect -f '{{.State.Running}}' "$CONTAINER_NAME")" = "true" ]; then
  echo "Container $CONTAINER_NAME is running successfully!"
else
  echo "Failed to start container $CONTAINER_NAME"
  docker logs "$CONTAINER_NAME"
  exit 1
fi

# Wait for the application to be healthy
echo "Waiting for the application to be healthy..."
HEALTH_CHECK_RETRIES=10
HEALTH_CHECK_INTERVAL=5

for i in $(seq 1 $HEALTH_CHECK_RETRIES); do
  if curl -s "http://localhost:$HOST_PORT/health" | grep -q "UP"; then
    echo "Application is healthy!"
    exit 0
  else
    echo "Health check failed, retry $i/$HEALTH_CHECK_RETRIES"
    sleep $HEALTH_CHECK_INTERVAL
  fi
done

echo "Application failed to become healthy after $HEALTH_CHECK_RETRIES retries"
docker logs "$CONTAINER_NAME"
exit 1