FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java", "-jar", "target/nexufy-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
