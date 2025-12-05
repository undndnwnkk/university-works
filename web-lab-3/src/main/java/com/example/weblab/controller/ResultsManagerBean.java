package com.example.weblab.controller;

import com.example.weblab.dto.CheckResultDto;
import com.example.weblab.service.AreaCheckService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@ApplicationScoped
public class ResultsManagerBean {

    @Inject
    private AreaCheckService service;
    private List<CheckResultDto> results;


    @PostConstruct
    public void init() {
        results = new CopyOnWriteArrayList<>(service.getAllResults());
    }

    @Transactional
    public void addResult(CheckResultDto result) {
        results.add(0, result);
    }

    public List<CheckResultDto> getResults() {
        return results;
    }
}
