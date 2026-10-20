package ru.itmo.routes.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.assertj.core.api.Assertions.assertThat;

class DomainConstraintsTest {
    @Test
    void routeFieldsExposeRequiredBeanValidationConstraints() throws Exception {
        assertThat(field(Route.class, "name").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(field(Route.class, "coordinates").isAnnotationPresent(NotNull.class)).isTrue();
        assertThat(field(Route.class, "from").isAnnotationPresent(NotNull.class)).isTrue();
        assertThat(field(Route.class, "distance").getAnnotation(DecimalMin.class).value()).isEqualTo("1.0");
        assertThat(field(Route.class, "rating").isAnnotationPresent(Positive.class)).isTrue();
    }

    @Test
    void identifiersAreGeneratedByDatabaseIdentity() throws Exception {
        GeneratedValue routeId = field(Route.class, "id").getAnnotation(GeneratedValue.class);
        GeneratedValue locationId = field(Location.class, "id").getAnnotation(GeneratedValue.class);
        assertThat(routeId.strategy()).isEqualTo(GenerationType.IDENTITY);
        assertThat(locationId.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    @Test
    void entitySettersNormalizeUserText() {
        Route route = new Route();
        route.setName("  Тестовый маршрут  ");
        Location location = new Location();
        location.setName("   ");
        assertThat(route.getName()).isEqualTo("Тестовый маршрут");
        assertThat(location.getName()).isNull();
    }

    private Field field(Class<?> type, String name) throws NoSuchFieldException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
