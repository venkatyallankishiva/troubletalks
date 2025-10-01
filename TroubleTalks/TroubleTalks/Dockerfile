# === STAGE 1: Build the JAR ===
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set the working directory inside the container
WORKDIR /app
# Copy the pom.xml file to download dependencies first
COPY pom.xml .
# Download dependencies (uses cached layer for speed)
RUN mvn dependency:go-offline
# Copy the rest of the source code
COPY src ./src
# Package the application into a JAR
RUN mvn clean package -DskipTests

# === STAGE 2: Create the final, lightweight image ===
# Use a smaller JRE image for the final runtime
FROM eclipse-temurin:17-jre-alpine
# Expose the default Spring Boot port
EXPOSE 8080
# Set the working directory
WORKDIR /app
# Copy the JAR file from the build stage
COPY --from=build /app/target/TroubleTalks-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
