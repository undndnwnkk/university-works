package ru.itmo.routes.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.itmo.routes.domain.Location;
import ru.itmo.routes.domain.Route;
import ru.itmo.routes.repository.RouteRepository;
import ru.itmo.routes.websocket.RouteEvents;
import java.util.List;
import java.util.Optional;

@Stateless
public class RouteService {
    @Inject private RouteRepository repository;

    public List<Route> page(String filter, String sort, boolean ascending, int page, int pageSize) {
        return repository.findPage(filter, sort, ascending, Math.max(0, page) * pageSize, pageSize);
    }
    public long count(String filter) { return repository.count(filter); }
    public Optional<Route> find(Integer id) { return id == null ? Optional.empty() : repository.findById(id); }
    public List<Location> locations() { return repository.findLocations(); }
    public List<Route> all() { return repository.findAll(); }

    public Route create(Route route, Integer fromId, Integer toId) {
        bindLocations(route, fromId, toId);
        Route saved = repository.save(route);
        RouteEvents.broadcast("created", saved.getId());
        return saved;
    }

    public Route update(Integer id, Route input, Integer fromId, Integer toId) {
        Route stored = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Маршрут не найден"));
        stored.setName(input.getName());
        stored.setCoordinates(input.getCoordinates());
        stored.setDistance(input.getDistance());
        stored.setRating(input.getRating());
        bindLocations(stored, fromId, toId);
        Route updated = repository.update(stored);
        RouteEvents.broadcast("updated", updated.getId());
        return updated;
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
    public Optional<Route> maxRating() { return repository.maxRating(); }
    public List<Route> ratingLessThan(int rating) { return repository.ratingLessThan(rating); }
    public List<Route> findBetween(Integer from, Integer to, String sort) { return repository.findBetween(from, to, sort); }

    public Integer addBetween(String name, double cx, long cy, Integer from, Integer to, float distance, int rating) {
        Integer id = repository.addBetween(name.trim(), cx, cy, from, to, distance, rating);
        RouteEvents.broadcast("created", id);
        return id;
    }

    private void bindLocations(Route route, Integer fromId, Integer toId) {
        if (fromId == null) throw new IllegalArgumentException("Выберите начальную локацию");
        route.setFrom(repository.locationReference(fromId));
        route.setTo(repository.locationReference(toId));
    }
}
