# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Add maintainer info
LABEL maintainer = "boussettahichem@yahoo.fr"

#Add a volume pointing to /tmp
VOLUME /tmp

ENV SPRING_APPLICATION_JSON ""

# Expose application port
EXPOSE 8080

ARG JAR_FILE

# Add application jar file to the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

