package com.example.weblab.beans;

import com.example.weblab.model.CheckResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Named
@ApplicationScoped
public class ResultsManagerBean {

    @PersistenceContext
    private EntityManager entityManager;
    private List<CheckResult> results;

    @PostConstruct
    public void init() {
        results = entityManager.createQuery("SELECT r FROM CheckResult r ORDER BY r.checkTimestamp DESC", CheckResult.class).getResultList();
    }

    @Transactional
    public void addResult(CheckResult result) {
        entityManager.persist(result);
        results.add(0, result);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public List<CheckResult> getResults() {
        return results;
    }
}
