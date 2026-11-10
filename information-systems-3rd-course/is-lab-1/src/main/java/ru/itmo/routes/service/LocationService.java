package ru.itmo.routes.service;

import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import ru.itmo.routes.domain.Location;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.dto.LocationForm;
import ru.itmo.routes.dto.LocationSummaryDto;
import ru.itmo.routes.repository.LocationRepository;
import java.util.List;
import java.util.Optional;

@Stateless
public class LocationService {
    @Inject private LocationRepository repository;
    @Inject private Event<LocationChanged> changes;

    public List<LocationDto> all() { return RouteMapper.toLocationDtos(repository.findAll()); }

    public List<LocationSummaryDto> summaries() {
        return repository.findAllWithRouteCount().stream()
                .map(row -> new LocationSummaryDto(RouteMapper.toDto((Location) row[0]), ((Number) row[1]).longValue()))
                .toList();
    }

    public Optional<LocationDto> find(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id).map(RouteMapper::toDto);
    }

    public long routeCount(Integer id) { return repository.routeCount(id); }

    public LocationDto create(LocationForm form) {
        Location location = new Location();
        RouteMapper.copy(form, location);
        Location saved = repository.save(location);
        changes.fire(new LocationChanged("created", saved.getId()));
        return RouteMapper.toDto(saved);
    }

    public LocationDto update(Integer id, LocationForm form) {
        Location stored = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Локация не найдена"));
        RouteMapper.copy(form, stored);
        changes.fire(new LocationChanged("updated", id));
        return RouteMapper.toDto(stored);
    }

    public int delete(Integer id, Integer replacementId) {
        if (id != null && id.equals(replacementId)) throw new IllegalArgumentException("Локация не может заменять сама себя");
        int moved = repository.deleteWithReplacement(id, replacementId);
        changes.fire(new LocationChanged("deleted", id));
        return moved;
    }
}
