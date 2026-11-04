#!/bin/bash
# Starts the Student Placement Management System on the H2 in memory database.
# Nothing has to be installed for this - the data comes from data.sql.
#
# Usage:  ./run.sh          -> http://localhost:8080
#         ./run.sh 8099     -> http://localhost:8099
set -e
cd "$(dirname "$0")"

# Use the Maven wrapper so only a JDK 17+ is needed.
MVN="./mvnw"

PORT="${1:-8080}"
echo "Building..."
$MVN -q clean package
echo "Starting on http://localhost:$PORT   (database: H2 in memory)"
java -jar target/placement-portal.jar --server.port="$PORT"
