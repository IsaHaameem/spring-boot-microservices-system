# ---- Stage 1: build with Maven ----
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build

COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---- Stage 2: minimal runtime ----
FROM eclipse-temurin:21-jre
RUN groupadd -r appuser && useradd -r -g appuser appuser
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

USER appuser

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]