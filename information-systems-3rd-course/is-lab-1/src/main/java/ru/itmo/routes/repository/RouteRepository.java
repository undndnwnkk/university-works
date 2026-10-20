package ru.itmo.routes.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ru.itmo.routes.domain.Location;
import ru.itmo.routes.domain.Route;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RouteRepository {
    @PersistenceContext(unitName = "routesPU")
    private EntityManager em;

    public Optional<Route> findById(Integer id) {
        return Optional.ofNullable(em.find(Route.class, id));
    }

    public List<Route> findPage(String filter, String sort, boolean ascending, int offset, int limit) {
        String sortColumn = switch (sort == null ? "creationDate" : sort) {
            case "name" -> "r.name";
            case "from" -> "f.name";
            case "to" -> "t.name";
            case "distance" -> "r.distance";
            case "rating" -> "r.rating";
            case "id" -> "r.id";
            default -> "r.creationDate";
        };
        String query = "select r from Route r join r.from f left join r.to t "
                + "where (:filter = '' or lower(r.name) like :pattern or lower(coalesce(f.name,'')) like :pattern "
                + "or lower(coalesce(t.name,'')) like :pattern) order by " + sortColumn + (ascending ? " asc" : " desc");
        return em.createQuery(query, Route.class)
                .setParameter("filter", normalized(filter))
                .setParameter("pattern", "%" + normalized(filter) + "%")
                .setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    public long count(String filter) {
        return em.createQuery("select count(r) from Route r join r.from f left join r.to t "
                        + "where (:filter = '' or lower(r.name) like :pattern or lower(coalesce(f.name,'')) like :pattern "
                        + "or lower(coalesce(t.name,'')) like :pattern)", Long.class)
                .setParameter("filter", normalized(filter))
                .setParameter("pattern", "%" + normalized(filter) + "%")
                .getSingleResult();
    }

    public List<Location> findLocations() {
        return em.createQuery("select l from Location l order by coalesce(l.name, ''), l.id", Location.class).getResultList();
    }

    public List<Route> findAll() {
        return em.createQuery("select r from Route r order by r.name, r.id", Route.class).getResultList();
    }

    public Location locationReference(Integer id) { return id == null ? null : em.getReference(Location.class, id); }
    public Route save(Route route) { em.persist(route); return route; }
    public Route update(Route route) { return em.merge(route); }

    public void deleteWithReplacement(Integer routeId, Integer replacementId) {
        em.createNativeQuery("select delete_route_with_replacement(?1, ?2)")
                .setParameter(1, routeId).setParameter(2, replacementId).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Route> ratingLessThan(int rating) {
        return em.createNativeQuery("select * from routes_rating_less_than(?1)", Route.class)
                .setParameter(1, rating).getResultList();
    }

    @SuppressWarnings("unchecked")
    public Optional<Route> maxRating() {
        List<Route> rows = em.createNativeQuery("select * from max_rating_route()", Route.class)
                .setMaxResults(1).getResultList();
        return rows.stream().findFirst();
    }

    public Integer deleteOneByRating(int rating) {
        Object value = em.createNativeQuery("select delete_one_route_by_rating(?1)")
                .setParameter(1, rating).getSingleResult();
        return value == null ? null : ((Number) value).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Route> findBetween(Integer fromId, Integer toId, String sort) {
        return em.createNativeQuery("select * from find_routes_between(?1, ?2, ?3)", Route.class)
                .setParameter(1, fromId).setParameter(2, toId).setParameter(3, sort).getResultList();
    }

    public Integer addBetween(String name, double cx, long cy, Integer fromId, Integer toId,
                              float distance, int rating) {
        return ((Number) em.createNativeQuery("select add_route_between(?1, ?2, ?3, ?4, ?5, ?6, ?7)")
                .setParameter(1, name).setParameter(2, cx).setParameter(3, cy)
                .setParameter(4, fromId).setParameter(5, toId).setParameter(6, distance)
                .setParameter(7, rating).getSingleResult()).intValue();
    }

    private String normalized(String value) { return value == null ? "" : value.trim().toLowerCase(); }
}
