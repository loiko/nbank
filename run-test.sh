#!/bin/bash

IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api}
HOST_IP=${2:-192.168.0.101}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

echo "Using HOST IP: $HOST_IP"
echo "Test profile: $TEST_PROFILE"

docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://$HOST_IP:4111 \
  -e UIBASEURL=http://$HOST_IP:3000 \
  -e DB_URL=jdbc:postgresql://$HOST_IP:5433/nbank \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
$IMAGE_NAME

echo ">>> Tests completed"
echo "Log file: $TEST_OUTPUT_DIR/logs/run.log"
echo "Test results: $TEST_OUTPUT_DIR/results"
echo "Report: $TEST_OUTPUT_DIR/report"