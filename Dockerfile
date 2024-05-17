# Use a base image with Java
FROM openjdk:11-jdk-alpine


ARG JAR_FILE=target/*.jar
# Set the working directory inside the container
WORKDIR /app

# Copy the executable JAR file into the container
COPY ${JAR_FILE} app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
