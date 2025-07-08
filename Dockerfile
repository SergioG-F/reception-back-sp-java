FROM eclipse-temurin:24-jdk-alpine
COPY target/recepcion-0.0.1-SNAPSHOT.jar /api-recepcion.jar
ENTRYPOINT ["java", "-jar", "api-recepcion.jar"]
