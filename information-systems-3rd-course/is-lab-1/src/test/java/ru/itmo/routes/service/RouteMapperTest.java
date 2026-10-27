package ru.itmo.routes.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;
import ru.itmo.routes.domain.Coordinates;
import ru.itmo.routes.domain.Location;
import ru.itmo.routes.domain.Route;
import ru.itmo.routes.dto.RouteDto;
import ru.itmo.routes.dto.RouteForm;
import java.lang.reflect.Field;
import static org.assertj.core.api.Assertions.assertThat;

class RouteMapperTest {
    @Test
    void entityIsMappedToDtoWithLocations() {
        Route route = new Route();
        route.setName("Маршрут");
        route.setCoordinates(new Coordinates(1.5, 2L));
        route.setFrom(new Location(1, 2.0, 3f, "Старт"));
        route.setDistance(10f);
        route.setRating(5);

        RouteDto dto = RouteMapper.toDto(route);

        assertThat(dto.getName()).isEqualTo("Маршрут");
        assertThat(dto.getCoordinates().getX()).isEqualTo(1.5);
        assertThat(dto.getCoordinates().getY()).isEqualTo(2L);
        assertThat(dto.getFrom().getDisplayName()).isEqualTo("Старт");
        assertThat(dto.getFrom().getZ()).isEqualTo(3f);
        assertThat(dto.getTo()).isNull();
        assertThat(dto.getRating()).isEqualTo(5);
    }

    @Test
    void formIsCopiedOntoEntity() {
        RouteForm form = new RouteForm();
        form.setName("  Новый  ");
        form.setCoordinateX(4.0);
        form.setCoordinateY(7L);
        form.setDistance(3f);
        form.setRating(2);
        Route route = new Route();

        RouteMapper.copy(form, route);

        assertThat(route.getName()).isEqualTo("Новый");
        assertThat(route.getCoordinates().getX()).isEqualTo(4.0);
        assertThat(route.getCoordinates().getY()).isEqualTo(7L);
        assertThat(route.getDistance()).isEqualTo(3f);
        assertThat(route.getRating()).isEqualTo(2);
    }

    @Test
    void formRepeatsEntityConstraints() throws Exception {
        assertThat(field("name").isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(field("fromId").isAnnotationPresent(NotNull.class)).isTrue();
        assertThat(field("distance").getAnnotation(DecimalMin.class).value()).isEqualTo("1.0");
        assertThat(field("rating").isAnnotationPresent(Positive.class)).isTrue();
    }

    private Field field(String name) throws NoSuchFieldException {
        return RouteForm.class.getDeclaredField(name);
    }
}
