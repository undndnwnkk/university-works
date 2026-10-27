package ru.itmo.routes.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RouteDto implements Serializable {
    private final Integer id;
    private final String name;
    private final CoordinatesDto coordinates;
    private final LocalDateTime creationDate;
    private final LocationDto from;
    private final LocationDto to;
    private final float distance;
    private final Integer rating;

    public RouteDto(Integer id, String name, CoordinatesDto coordinates, LocalDateTime creationDate,
                    LocationDto from, LocationDto to, float distance, Integer rating) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.rating = rating;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public CoordinatesDto getCoordinates() { return coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public LocationDto getFrom() { return from; }
    public LocationDto getTo() { return to; }
    public float getDistance() { return distance; }
    public Integer getRating() { return rating; }
}
