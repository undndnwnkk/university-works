package com.web.weblab4.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.UUID;

@Entity
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "x_value", nullable = false)
    @DecimalMin(value = "-5", message = "X must be greater than or equal to -5")
    @DecimalMax(value = "3", message = "X must be less than or equal to 3")
    private double x;

    @Column(name = "y_value", nullable = false)
    @DecimalMin(value = "-3", message = "Y must be greater than or equal to -3")
    @DecimalMax(value = "3", message = "Y must be less than or equal to 3")
    private double y;

    @Column(name = "r_value", nullable = false)
    @DecimalMin(value = "-5", message = "R must be greater than or equal to -5")
    @DecimalMax(value = "3", message = "R must be less than or equal to 3")
    private double r;

    @Column(name = "hit", nullable = false)
    private boolean hit;

    @Column(name = "execution_time", nullable = false)
    private long executionTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
