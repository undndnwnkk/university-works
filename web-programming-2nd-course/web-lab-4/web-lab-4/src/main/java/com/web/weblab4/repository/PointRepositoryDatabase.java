package com.web.weblab4.repository;

import com.web.weblab4.model.Point;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

@Stateless
public class PointRepositoryDatabase implements PointRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Point point) {
        em.persist(point);
    }

    @Override
    public List<Point> findByUserId(UUID userId) {
        return em.createQuery("SELECT p FROM Point p WHERE p.owner.id = :userId", Point.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Point> findAll() {
        return em.createQuery(
                "SELECT p FROM Point p", Point.class
        ).getResultList();
    }
}
