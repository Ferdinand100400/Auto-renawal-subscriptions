FROM amazoncorretto:21 AS build

WORKDIR /app

# Копируем Gradle файлы
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew .

# Копируем исходники
COPY src src

# Даем права на выполнение и собираем JAR
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon

# запуск
FROM amazoncorretto:21

WORKDIR /app

# Копируем собранный JAR
COPY --from=build /app/build/libs/*.jar app.jar

# Ждем БД, запускаем миграции и приложение
ENTRYPOINT ["java", "-jar", "app.jar"]