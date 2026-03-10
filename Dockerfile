

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the package (skipping tests for speed)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE #8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# cache image
#FROM maven:3.9.6-eclipse-temurin-17

# Set working directory
#WORKDIR /app

# Copy a minimal pom.xml that contains all common dependencies
#COPY pom.xml .

# Preload dependencies offline
#RUN mvn dependency:go-offline