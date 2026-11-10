package ru.itmo.routes.dto;

import java.io.Serializable;

public class LocationSummaryDto implements Serializable {
    private final LocationDto location;
    private final long routeCount;

    public LocationSummaryDto(LocationDto location, long routeCount) {
        this.location = location;
        this.routeCount = routeCount;
    }

    public LocationDto getLocation() { return location; }
    public long getRouteCount() { return routeCount; }
}
