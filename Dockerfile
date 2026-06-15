# Step 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests
 
# Step 2: Run the compiled application jar using a supported runtime
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]