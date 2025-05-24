FROM openjdk:25-slim
WORKDIR /app/electronicsstore-springboot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]