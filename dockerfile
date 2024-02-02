FROM openjdk:17-jdk-alpine
COPY target/ms-register-0.0.1-SNAPSHOT.jar ms-register-app.jar
ENTRYPOINT ["java","-jar","ms-register-app.jar"]