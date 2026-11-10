package ru.itmo.routes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class LocationForm implements Serializable {
    @Size(max = 755, message = "Название локации не длиннее 755 символов")
    private String name;

    @NotNull(message = "Координата X обязательна")
    private Integer x = 0;

    @NotNull(message = "Координата Y обязательна")
    private Double y = 0.0;

    @NotNull(message = "Координата Z обязательна")
    private Float z = 0f;

    public static LocationForm of(LocationDto location) {
        LocationForm form = new LocationForm();
        form.name = location.getName();
        form.x = location.getX();
        form.y = location.getY();
        form.z = location.getZ();
        return form;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Float getZ() { return z; }
    public void setZ(Float z) { this.z = z; }
}
