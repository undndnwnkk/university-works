package ru.itmo.routes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class Coordinates implements Serializable {
    @NotNull
    @Column(name = "coordinate_x", nullable = false)
    private Double x;

    @NotNull
    @Column(name = "coordinate_y", nullable = false)
    private Long y;

    public Coordinates() {}

    public Coordinates(Double x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Long getY() { return y; }
    public void setY(Long y) { this.y = y; }
}
