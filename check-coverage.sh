#!/bin/bash

MIN_COVERAGE=50

REPORT_FILE="swagger-coverage-report.html"

if [ ! -f "$REPORT_FILE" ]; then
    echo "Swagger coverage report not found at $REPORT_FILE"
    exit 1
fi

COVERAGE=$(grep -oP 'Full coverage: \K[0-9]+(\.[0-9]+)?' "$REPORT_FILE" | head -1)

if [ -z "$COVERAGE" ]; then
    echo "Could not extract coverage percentage from report"
    exit 1
fi

echo "API Coverage: ${COVERAGE}%"
echo "Minimum required: ${MIN_COVERAGE}%"

COVERAGE_INT=${COVERAGE%.*}

if [ "$COVERAGE_INT" -lt "$MIN_COVERAGE" ]; then
    echo "QUALITY GATE FAILED: Coverage ${COVERAGE}% is below minimum ${MIN_COVERAGE}%"
    exit 1
else
    echo "QUALITY GATE PASSED: Coverage ${COVERAGE}% meets minimum requirement"
    exit 0
fi