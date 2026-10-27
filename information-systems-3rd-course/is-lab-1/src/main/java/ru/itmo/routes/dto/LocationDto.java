package ru.itmo.routes.dto;

import java.io.Serializable;

public class LocationDto implements Serializable {
    private final Integer id;
    private final int x;
    private final double y;
    private final Float z;
    private final String name;

    public LocationDto(Integer id, int x, double y, Float z, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Integer getId() { return id; }
    public int getX() { return x; }
    public double getY() { return y; }
    public Float getZ() { return z; }
    public String getName() { return name; }

    public String getDisplayName() {
        return name == null ? "Локация #" + id : name;
    }
}
