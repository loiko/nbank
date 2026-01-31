#!/bin/bash

if [ -f .env ]; then
    echo "Loading environment from .env file..."
    set -a
    source .env
    set +a
else
    echo "Warning: .env file not found!"
fi

IMAGE_NAME="nbank-tests"
DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-lmikalai}"
TAG=${1:-latest}

REMOTE_IMAGE="$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

if [ -z "$DOCKERHUB_TOKEN" ]; then
    echo "Error: DOCKERHUB_TOKEN is not set!"
    echo "Please check your .env file."
    exit 1
fi

echo "======================================"
echo "Docker Image Push Script"
echo "======================================"
echo "Image: $IMAGE_NAME"
echo "Docker Hub User: $DOCKERHUB_USERNAME"
echo "Tag: $TAG"
echo "======================================"

echo "Step 1: Building Docker image..."
docker build -t $IMAGE_NAME .

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi
echo "Build successful"

echo "Step 2: Tagging image as $REMOTE_IMAGE"
docker tag $IMAGE_NAME $REMOTE_IMAGE
echo "Tagged successfully"

echo "Step 3: Logging in to Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login -u $DOCKERHUB_USERNAME --password-stdin

if [ $? -ne 0 ]; then
    echo "Login failed!"
    exit 1
fi
echo "Logged in successfully"

echo "Step 4: Pushing image to Docker Hub..."
docker push $REMOTE_IMAGE

if [ $? -ne 0 ]; then
    echo "Push failed!"
    exit 1
fi

echo "======================================"
echo "Successfully pushed to Docker Hub!"
echo "======================================"
echo "To pull this image on another machine, run:"
echo "   docker pull $REMOTE_IMAGE"
echo "To run tests with this image:"
echo "   docker run --rm \\"
echo "     $REMOTE_IMAGE"
echo "======================================"