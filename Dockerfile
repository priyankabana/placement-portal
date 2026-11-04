# ---------- stage 1: build the jar with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Download dependencies first, so this layer is cached until pom.xml changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Tests run in CI; the image build only packages the jar
RUN mvn -B -q package -DskipTests

# ---------- stage 2: small runtime image with only the JRE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as an unprivileged user, not root
RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /app/target/placement-portal.jar app.jar
USER app

# The app reads $PORT (server.port=${PORT:8080}); 8080 is the local default
ENV PORT=8080
EXPOSE 8080

# Keep the heap inside the container memory limit (512 MB on the free tier)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
