package ru.itmo.routes.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.itmo.routes.domain.Route;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.dto.RouteDto;
import ru.itmo.routes.dto.RouteForm;
import ru.itmo.routes.repository.RouteRepository;
import ru.itmo.routes.websocket.RouteEvents;
import java.util.List;
import java.util.Optional;

@Stateless
public class RouteService {
    @Inject private RouteRepository repository;

    public List<RouteDto> page(String filter, String sort, boolean ascending, int page, int pageSize) {
        return RouteMapper.toRouteDtos(repository.findPage(filter, sort, ascending, Math.max(0, page) * pageSize, pageSize));
    }
    public long count(String filter) { return repository.count(filter); }
    public Optional<RouteDto> find(Integer id) {
        return id == null ? Optional.empty() : repository.findById(id).map(RouteMapper::toDto);
    }
    public List<LocationDto> locations() { return RouteMapper.toLocationDtos(repository.findLocations()); }
    public List<RouteDto> all() { return RouteMapper.toRouteDtos(repository.findAll()); }

    public RouteDto create(RouteForm form) {
        Route route = new Route();
        RouteMapper.copy(form, route);
        bindLocations(route, form);
        Route saved = repository.save(route);
        RouteEvents.broadcast("created", saved.getId());
        return RouteMapper.toDto(saved);
    }

    public RouteDto update(Integer id, RouteForm form) {
        Route stored = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Маршрут не найден"));
        RouteMapper.copy(form, stored);
        bindLocations(stored, form);
        Route updated = repository.update(stored);
        RouteEvents.broadcast("updated", updated.getId());
        return RouteMapper.toDto(updated);
    }

    public void delete(Integer id, Integer replacementId) {
        if (id != null && id.equals(replacementId)) throw new IllegalArgumentException("Замена должна отличаться от удаляемого маршрута");
        repository.deleteWithReplacement(id, replacementId);
        RouteEvents.broadcast("deleted", id);
    }

    public Integer deleteOneByRating(int rating) {
        Integer id = repository.deleteOneByRating(rating);
        if (id != null) RouteEvents.broadcast("deleted", id);
        return id;
    }
    public Optional<RouteDto> maxRating() { return repository.maxRating().map(RouteMapper::toDto); }
    public List<RouteDto> ratingLessThan(int rating) { return RouteMapper.toRouteDtos(repository.ratingLessThan(rating)); }
    public List<RouteDto> findBetween(Integer from, Integer to, String sort) {
        return RouteMapper.toRouteDtos(repository.findBetween(from, to, sort));
    }

    public Integer addBetween(String name, double cx, long cy, Integer from, Integer to, float distance, int rating) {
        Integer id = repository.addBetween(name.trim(), cx, cy, from, to, distance, rating);
        RouteEvents.broadcast("created", id);
        return id;
    }

    private void bindLocations(Route route, RouteForm form) {
        if (form.getFromId() == null) throw new IllegalArgumentException("Выберите начальную локацию");
        route.setFrom(repository.locationReference(form.getFromId()));
        route.setTo(repository.locationReference(form.getToId()));
    }
}
