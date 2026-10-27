package ru.itmo.routes.migration;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MigrationScriptsTest {
    @Test
    void versionedMigrationsAreOnClasspath() {
        ClassLoader loader = getClass().getClassLoader();
        assertThat(loader.getResource("db/migration/V1__schema.sql")).isNotNull();
        assertThat(loader.getResource("db/migration/V2__seed_data.sql")).isNotNull();
    }

    @Test
    void flywayScansConfiguredLocation() {
        assertThat(DatabaseMigration.configure(null).getConfiguration().getLocations())
                .extracting(Object::toString)
                .containsExactly("classpath:db/migration");
    }
}
