package ru.itmo.routes.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.itmo.routes.domain.Location;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationRepository {
    @PersistenceContext(unitName = "routesPU")
    private EntityManager em;

    public Optional<Location> findById(Integer id) {
        return Optional.ofNullable(em.find(Location.class, id));
    }

    public List<Location> findAll() {
        return em.createQuery("select l from Location l order by coalesce(l.name, ''), l.id", Location.class).getResultList();
    }

    public List<Object[]> findAllWithRouteCount() {
        return em.createQuery("select l, (select count(r) from Route r where r.from = l or r.to = l) "
                + "from Location l order by l.id", Object[].class).getResultList();
    }

    public long routeCount(Integer id) {
        return em.createQuery("select count(r) from Route r where r.from.id = :id or r.to.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
    }

    public Location reference(Integer id) { return id == null ? null : em.getReference(Location.class, id); }
    public Location save(Location location) { em.persist(location); return location; }

    public int deleteWithReplacement(Integer id, Integer replacementId) {
        Object moved = em.createNativeQuery("select delete_location_with_replacement(?1, ?2)")
                .setParameter(1, id).setParameter(2, replacementId).getSingleResult();
        em.clear();
        return ((Number) moved).intValue();
    }
}
