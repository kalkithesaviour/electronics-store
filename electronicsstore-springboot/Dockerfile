FROM openjdk:25-slim

# Set correct project root path inside the container
WORKDIR /app/electronicsstore-springboot

# Copy the JAR file into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
