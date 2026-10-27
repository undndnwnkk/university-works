package ru.itmo.routes.migration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import javax.sql.DataSource;

@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DatabaseMigration {
    @Resource(lookup = "java:/jdbc/RouteDS")
    private DataSource dataSource;

    @PersistenceUnit(unitName = "routesPU")
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    void migrate() {
        configure(dataSource).migrate();
        entityManagerFactory.unwrap(SessionFactory.class).getSchemaManager().validateMappedObjects();
    }

    static Flyway configure(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();
    }
}
