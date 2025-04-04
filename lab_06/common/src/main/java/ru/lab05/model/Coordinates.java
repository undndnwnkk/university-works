package ru.lab05.model;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private float x;
    private float y; // Значение поля должно быть больше -46

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}
