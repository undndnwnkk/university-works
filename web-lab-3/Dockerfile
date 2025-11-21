# 1. Используем тот же официальный образ WildFly с JDK 17
FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17

# 2. Скачиваем драйвер PostgreSQL отдельно.
RUN curl -o /tmp/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

# 3. Запускаем jboss-cli в режиме встроенного сервера для изменения конфигурации ОФФЛАЙН
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --commands=" \
    embed-server --server-config=standalone-full.xml, \
    batch, \
    module add --name=org.postgres --resources=/tmp/postgresql.jar --dependencies=jakarta.transaction.api, \
    /subsystem=datasources/jdbc-driver=postgres:add(driver-name=postgres,driver-module-name=org.postgres,driver-class-name=org.postgresql.Driver), \
    data-source add --name=PostgresDS --jndi-name=java:jboss/datasources/PostgresDS --driver-name=postgres --connection-url=jdbc:postgresql://db:5432/weblab --user-name=myuser --password=mypassword --enabled=true, \
    run-batch, \
    stop-embedded-server \
"

# 4. Копируем ваше собранное приложение в директорию для развертывания
COPY target/weblab-jsf.war /opt/jboss/wildfly/standalone/deployments/

# 5. Создаем пользователя для доступа к админ-панели
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

# 6. Запускаем WildFly, используя уже измененную конфигурацию
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c", "standalone-full.xml"]