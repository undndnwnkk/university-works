package org.example;

public class Request {
    Double x;
    Double y;
    Double r;

    public Request() {}

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

    public Request(Double x, Double y, Double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public String toString() {
        return "Request{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                '}';
    }
}
