package com.example.weblab.controller;

import com.example.weblab.dto.CheckResultDto;
import com.example.weblab.dto.CoordinatesDto;
import com.example.weblab.model.CheckResult;
import com.example.weblab.service.AreaCheckService;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.time.LocalDateTime;

@Named
@ViewScoped
public class PointBean implements Serializable {
    private Double x;
    private Double y;
    private Double r;

    @Inject
    private AreaCheckService service;

    @Inject
    private ResultsManagerBean managerBean;

    public void checkHit() {
        if (x != null || y != null || r != null) {
            CoordinatesDto input = new CoordinatesDto(x, y, r);
            CheckResultDto checkResultDto = service.checkPoint(input);
            managerBean.addResult(checkResultDto);
        }
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public ResultsManagerBean getResultsManagerBean() {
        return managerBean;
    }

    public void setResultsManagerBean(ResultsManagerBean resultsManagerBean) {
        this.managerBean = resultsManagerBean;
    }
}
