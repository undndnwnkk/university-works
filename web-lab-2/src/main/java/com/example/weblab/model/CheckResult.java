package com.example.weblab.model;

import java.io.Serializable;

public class CheckResult implements Serializable {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private String currentTime;
    private long executionTime;

    public CheckResult(double x, double y, double r, boolean hit, String currentTime, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.currentTime = currentTime;
        this.executionTime = executionTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public boolean isHit() {
        return hit;
    }

    public double getR() {
        return r;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }


}
