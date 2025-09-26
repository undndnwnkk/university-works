# Собираем наш сервер
FROM maven:3.8.4-openjdk-17 AS build
COPY --chown=maven:maven . /home/maven/src
WORKDIR /home/maven/src
RUN mvn clean package

FROM eclipse-temurin:17-jre
# Этот порт значения не имеет, выбираем любой
EXPOSE 1337
RUN mkdir /app
WORKDIR /app
COPY --from=build /home/maven/src/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-DFCGI_PORT=1337", "-jar", "app.jar"]
