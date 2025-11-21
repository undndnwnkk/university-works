package com.example.weblab.beans;

import com.example.weblab.model.CheckResult;
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
    private ResultsManagerBean resultsManagerBean;

    public void checkHit() {
        long startTime = System.nanoTime();
        boolean isHit = true;

        if (x <= 0 && y >= 0) {
            isHit = (y <= (x / 2 + r / 2));
        }
        else if (x <= 0 && y <= 0) {
            isHit = (x >= -r) && (y >= -r);
        }
        else if (x >= 0 && y <= 0) {
            isHit = (x * x + y * y <= (r / 2) * (r / 2));
        }

        long executionTime = System.nanoTime() - startTime;

        CheckResult checkResult = new CheckResult(
                this.x,
                this.y,
                this.r,
                isHit,
                LocalDateTime.now(),
                executionTime
        );

        resultsManagerBean.addResult(checkResult);
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
        return resultsManagerBean;
    }

    public void setResultsManagerBean(ResultsManagerBean resultsManagerBean) {
        this.resultsManagerBean = resultsManagerBean;
    }
}
