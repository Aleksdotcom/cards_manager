# Этап сборки
FROM eclipse-temurin:17.0.12_7-jdk-alpine AS builder
WORKDIR /app

# Копируем всё, что нужно для сборки
COPY . .

# Собираем jar-файл
RUN ./mvnw package -DskipTests

# Извлекаем слои из Spring Boot JAR
WORKDIR /extracted
RUN java -Djarmode=layertools -jar /app/target/*.jar extract

# Финальный образ
FROM eclipse-temurin:17.0.12_7-jdk-alpine
WORKDIR /application

# Копируем слои по частям — для оптимального кэширования
COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./

EXPOSE 8080

# Используем JarLauncher, как в Spring Boot fat jar
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
