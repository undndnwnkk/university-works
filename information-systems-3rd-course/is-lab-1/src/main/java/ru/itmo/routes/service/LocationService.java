package ru.itmo.routes.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.repository.LocationRepository;
import java.util.List;

@Stateless
public class LocationService {
    @Inject private LocationRepository repository;

    public List<LocationDto> all() { return RouteMapper.toLocationDtos(repository.findAll()); }
}
