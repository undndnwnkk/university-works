FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17

RUN curl -o /tmp/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --commands=" \
    embed-server --server-config=standalone-full.xml, \
    batch, \
    module add --name=org.postgres --resources=/tmp/postgresql.jar --dependencies=jakarta.transaction.api, \
    /subsystem=datasources/jdbc-driver=postgres:add(driver-name=postgres,driver-module-name=org.postgres,driver-class-name=org.postgresql.Driver), \
    data-source add --name=PostgresDS --jndi-name=java:jboss/datasources/PostgresDS --driver-name=postgres --connection-url=jdbc:postgresql://db:5432/weblab --user-name=myuser --password=mypassword --enabled=true, \
    run-batch, \
    stop-embedded-server \
"

COPY target/weblab-jsf.war /opt/jboss/wildfly/standalone/deployments/

RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c", "standalone-full.xml"]