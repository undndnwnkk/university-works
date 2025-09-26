FROM maven:3.8.4-openjdk-17 AS build

COPY --chown=maven:maven . /home/maven/src
WORKDIR /home/maven/src

COPY --chown=maven:maven libfscgi/fastcgi-lib-1.0.jar /home/maven/
RUN mvn install:install-file \
    -Dfile=/home/maven/fastcgi-lib-1.0.jar \
    -DgroupId=com.fastcgi \
    -DartifactId=fastcgi-lib \
    -Dversion=1.0 \
    -Dpackaging=jar

RUN mvn clean package

FROM eclipse-temurin:17-jre

EXPOSE 1337

RUN mkdir /app
WORKDIR /app

COPY --from=build /home/maven/src/libs/server-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar

ENTRYPOINT ["java", "-DFCGI_PORT=1337", "-jar", "app.jar"]
