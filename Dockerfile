FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 10000
CMD ["java", "-jar", "target/lers-backend-0.0.1-SNAPSHOT.jar"]