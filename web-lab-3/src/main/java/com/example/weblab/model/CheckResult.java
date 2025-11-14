package com.example.weblab.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
public class CheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x_value")
    private double x;

    @Column(name = "y_value")
    private double y;

    @Column(name = "r_value")
    private double r;

    @Column(name = "is_hit")
    private boolean hitResult;

    @Column(name = "created_at")
    private LocalDateTime checkTimestamp;

    @Column(name = "elapsed_ms")
    private long executionTime;

    public CheckResult() {
    }

    public CheckResult(double x, double y, double r, boolean hitResult, LocalDateTime checkTimestamp, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hitResult = hitResult;
        this.checkTimestamp = checkTimestamp;
        this.executionTime = executionTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isHitResult() {
        return hitResult;
    }

    public void setHitResult(boolean hitResult) {
        this.hitResult = hitResult;
    }

    public LocalDateTime getCheckTimestamp() {
        return checkTimestamp;
    }

    public void setCheckTimestamp(LocalDateTime checkTimestamp) {
        this.checkTimestamp = checkTimestamp;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
