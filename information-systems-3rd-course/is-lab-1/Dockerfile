FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package
RUN mvn -q -B dependency:get -Dartifact=org.postgresql:postgresql:42.7.5

FROM quay.io/wildfly/wildfly:35.0.1.Final-jdk17
COPY --chown=jboss:root docker/wildfly/modules/org/postgresql/main/module.xml /opt/jboss/wildfly/modules/org/postgresql/main/module.xml
COPY --from=build --chown=jboss:root /root/.m2/repository/org/postgresql/postgresql/42.7.5/postgresql-42.7.5.jar /opt/jboss/wildfly/modules/org/postgresql/main/postgresql.jar
COPY --chown=jboss:root docker/wildfly/configure.cli /tmp/configure.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configure.cli \
    && rm /tmp/configure.cli \
    && rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history \
    && mkdir -p /opt/jboss/wildfly/standalone/configuration/standalone_xml_history
COPY --from=build --chown=jboss:root /build/target/routes.war /opt/jboss/wildfly/standalone/deployments/routes.war
EXPOSE 8080
