package ru.itmo.routes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

public class RouteForm implements Serializable {
    @NotBlank(message = "Название обязательно")
    private String name;

    @NotNull(message = "Координата X обязательна")
    private Double coordinateX = 0.0;

    @NotNull(message = "Координата Y обязательна")
    private Long coordinateY = 0L;

    @NotNull(message = "Начальная локация обязательна")
    private Integer fromId;

    private Integer toId;

    @DecimalMin(value = "1.0", inclusive = false, message = "Расстояние должно быть больше 1")
    private float distance = 1.1f;

    @NotNull(message = "Рейтинг обязателен")
    @Positive(message = "Рейтинг должен быть больше 0")
    private Integer rating = 1;

    public static RouteForm of(RouteDto route) {
        RouteForm form = new RouteForm();
        form.name = route.getName();
        form.coordinateX = route.getCoordinates().getX();
        form.coordinateY = route.getCoordinates().getY();
        form.fromId = route.getFrom().getId();
        form.toId = route.getTo() == null ? null : route.getTo().getId();
        form.distance = route.getDistance();
        form.rating = route.getRating();
        return form;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getCoordinateX() { return coordinateX; }
    public void setCoordinateX(Double coordinateX) { this.coordinateX = coordinateX; }
    public Long getCoordinateY() { return coordinateY; }
    public void setCoordinateY(Long coordinateY) { this.coordinateY = coordinateY; }
    public Integer getFromId() { return fromId; }
    public void setFromId(Integer fromId) { this.fromId = fromId; }
    public Integer getToId() { return toId; }
    public void setToId(Integer toId) { this.toId = toId; }
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
