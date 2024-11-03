FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw  # Da permisos de ejecución a mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java", "-jar", "target/tu-aplicacion.jar"]
EXPOSE 8080
