package ru.itmo.routes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "locations")
public class Location implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private double y;

    @NotNull
    @Column(nullable = false)
    private Float z;

    @Size(max = 755)
    @Column(length = 755)
    private String name;

    public Location() {}

    public Location(int x, double y, Float z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Integer getId() { return id; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public Float getZ() { return z; }
    public void setZ(Float z) { this.z = z; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null || name.isBlank() ? null : name.trim(); }

    public String getDisplayName() {
        return name == null ? "Локация #" + id : name;
    }

    @Override public boolean equals(Object other) {
        return this == other || other instanceof Location that && id != null && Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return getClass().hashCode(); }
}
