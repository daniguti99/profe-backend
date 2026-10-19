# ---- Etapa de build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B -DskipTests clean package

# ---- Etapa de ejecución ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/profe-backend-0.0.1-SNAPSHOT.jar app.jar
# Límite de heap para el plan free (512 MB)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]