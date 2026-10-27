package ru.itmo.routes.service;

import ru.itmo.routes.domain.Coordinates;
import ru.itmo.routes.domain.Location;
import ru.itmo.routes.domain.Route;
import ru.itmo.routes.dto.CoordinatesDto;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.dto.RouteDto;
import ru.itmo.routes.dto.RouteForm;
import java.util.List;

final class RouteMapper {
    private RouteMapper() {}

    static RouteDto toDto(Route route) {
        Coordinates coordinates = route.getCoordinates();
        return new RouteDto(route.getId(), route.getName(),
                new CoordinatesDto(coordinates.getX(), coordinates.getY()), route.getCreationDate(),
                toDto(route.getFrom()), toDto(route.getTo()), route.getDistance(), route.getRating());
    }

    static LocationDto toDto(Location location) {
        return location == null ? null
                : new LocationDto(location.getId(), location.getX(), location.getY(), location.getZ(), location.getName());
    }

    static List<RouteDto> toRouteDtos(List<Route> routes) {
        return routes.stream().map(RouteMapper::toDto).toList();
    }

    static List<LocationDto> toLocationDtos(List<Location> locations) {
        return locations.stream().map(RouteMapper::toDto).toList();
    }

    /** Copies user input onto the entity; locations are bound by the service. */
    static void copy(RouteForm form, Route target) {
        target.setName(form.getName());
        target.setCoordinates(new Coordinates(form.getCoordinateX(), form.getCoordinateY()));
        target.setDistance(form.getDistance());
        target.setRating(form.getRating());
    }
}
