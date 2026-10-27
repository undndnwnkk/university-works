package ru.itmo.routes.dto;

import java.io.Serializable;

public class CoordinatesDto implements Serializable {
    private final Double x;
    private final Long y;

    public CoordinatesDto(Double x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() { return x; }
    public Long getY() { return y; }
}
