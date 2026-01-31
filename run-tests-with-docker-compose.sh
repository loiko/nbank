#!/bin/bash

set -e

IMAGE_NAME="nbank-tests"
TEST_PROFILE=${1:-all}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR="./test-output/$TIMESTAMP"
NETWORK="nbank-network"

echo "======================================"
echo "Running Tests with Docker Compose"
echo "======================================"
echo "Test profile: $TEST_PROFILE"
echo "Timestamp: $TIMESTAMP"
echo "======================================"

cleanup() {
    echo ""
    echo ">>> Stopping test environment..."
    docker compose -f infra/docker_compose/docker-compose.yml down
    echo ">>> Environment stopped"
}

trap cleanup EXIT

echo ">>> Creating output directories..."
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

echo ">>> Cleaning up previous environment..."
docker compose -f infra/docker_compose/docker-compose.yml down -v 2>/dev/null || true

echo ">>> Starting test environment and waiting for services..."
docker compose -f infra/docker_compose/docker-compose.yml up -d --wait

echo ">>> Services should be ready!"

echo ">>> Building test image..."
docker build -t $IMAGE_NAME .

echo ">>> Running tests..."
echo "======================================"

docker run --rm \
  --network $NETWORK \
  -v "$(pwd)/$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$(pwd)/$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$(pwd)/$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://backend:4111 \
  -e UIBASEURL=http://nginx:80 \
  -e SELENOID_URL=http://selenoid:4444 \
  -e SELENOID_UI_URL=http://selenoid-ui:8080 \
  -e DB_URL=jdbc:postgresql://postgres:5432/nbank \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  $IMAGE_NAME

TEST_EXIT_CODE=$?

echo ""
echo "======================================"
echo ">>> Tests completed"
echo "======================================"
echo "Exit code: $TEST_EXIT_CODE"
echo "Log file: $TEST_OUTPUT_DIR/logs/run.log"
echo "Test results: $TEST_OUTPUT_DIR/results"
echo "Report: $TEST_OUTPUT_DIR/report"
echo "======================================"

exit $TEST_EXIT_CODE