package ru.itmo.routes.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.itmo.routes.domain.Location;
import java.util.List;

@ApplicationScoped
public class LocationRepository {
    @PersistenceContext(unitName = "routesPU")
    private EntityManager em;

    public List<Location> findAll() {
        return em.createQuery("select l from Location l order by coalesce(l.name, ''), l.id", Location.class).getResultList();
    }

    public Location reference(Integer id) { return id == null ? null : em.getReference(Location.class, id); }
}
