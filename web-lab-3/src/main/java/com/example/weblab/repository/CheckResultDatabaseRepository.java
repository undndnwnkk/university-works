package com.example.weblab.repository;

import com.example.weblab.model.CheckResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class CheckResultDatabaseRepository implements CheckResultRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(CheckResult checkResult) {
        entityManager.persist(checkResult);
    }

    @Override
    public List<CheckResult> findAll() {
        return entityManager.createQuery(
                "SELECT r FROM CheckResult r ORDER BY r.checkTimestamp DESC", CheckResult.class
        ).getResultList();
    }
}
