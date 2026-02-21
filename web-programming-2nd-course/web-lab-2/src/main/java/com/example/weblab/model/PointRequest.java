package com.example.weblab.model;

public class PointRequest {
    private Double x_value;
    private Double y_value;
    private Double r_value;

    public PointRequest(){}

    public PointRequest(Double x_value, Double y_value, Double r_value) {
        this.x_value = x_value;
        this.y_value = y_value;
        this.r_value = r_value;
    }

    public Double getX_value() {
        return x_value;
    }

    public void setX_value(Double x_value) {
        this.x_value = x_value;
    }

    public Double getY_value() {
        return y_value;
    }

    public void setY_value(Double y_value) {
        this.y_value = y_value;
    }

    public Double getR_value() {
        return r_value;
    }

    public void setR_value(Double r_value) {
        this.r_value = r_value;
    }
}
