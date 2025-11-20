# Используем актуальный официальный образ Maven для сборки
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем весь проект в контейнер
COPY . .

# Собираем только нужный модуль (notification-service)
RUN mvn -pl notification-service clean package -DskipTests

# Используем актуальный официальный образ OpenJDK для запуска
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Копируем собранный jar из нужного модуля
COPY --from=build /app/notification-service/target/notification-service-1.0-SNAPSHOT.jar .

EXPOSE 8081
CMD ["java", "-jar", "notification-service-1.0-SNAPSHOT.jar"]
