# Etapa 1: Compilar el proyecto
FROM maven:3.9-eclipse-temurin-24-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar el jar
FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/recepcion-0.0.1-SNAPSHOT.jar /app/api-recepcion.jar
#ENTRYPOINT ["java", "-jar", "/app/api-recepcion.jar"]
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/api-recepcion.jar"]
