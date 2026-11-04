#!/bin/bash
# Starts the same application on MySQL instead of H2.
#
# Needs the MySQL server running and the database created once:
#   mysql -u root -e "CREATE DATABASE IF NOT EXISTS placement_db;
#     CREATE USER IF NOT EXISTS 'placement'@'localhost' IDENTIFIED BY '<your-password>';
#     GRANT ALL PRIVILEGES ON placement_db.* TO 'placement'@'localhost';"
#
# Usage:  ./run-mysql.sh          -> http://localhost:8080
#         ./run-mysql.sh 8099     -> http://localhost:8099
set -e
cd "$(dirname "$0")"

# Use the Maven wrapper so only a JDK 17+ is needed.
MVN="./mvnw"

PORT="${1:-8080}"

# DB_URL / DB_USERNAME / DB_PASSWORD can come from a local .env file (never committed)
if [ -f .env ]; then set -a; . ./.env; set +a; fi
if [ -z "${DB_PASSWORD:-}" ]; then
    echo "Set DB_PASSWORD (in .env or the environment) - see .env.example"
    exit 1
fi

# stop early with a clear message if the server is not up
if command -v mysqladmin >/dev/null 2>&1 && ! mysqladmin ping --silent 2>/dev/null; then
    echo "MySQL is not running. Start it with:  brew services start mysql"
    exit 1
fi

echo "Building..."
$MVN -q clean package
echo "Starting on http://localhost:$PORT   (database: MySQL placement_db)"
java -jar target/placement-portal.jar --spring.profiles.active=mysql --server.port="$PORT"
