package ru.itmo.routes.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.dto.RouteDto;
import ru.itmo.routes.service.RouteService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class SpecialOperationsBean implements Serializable {
    @Inject private RouteService service;
    private List<LocationDto> locations;
    private List<RouteDto> results = new ArrayList<>();
    private Integer rating = 1;
    private Integer fromId;
    private Integer toId;
    private String sort = "name";
    private String name;
    private double coordinateX;
    private long coordinateY;
    private float distance = 1.1f;
    private Integer newRating = 1;

    @PostConstruct public void init() { locations = service.locations(); }

    public void deleteByRating() {
        Integer id = service.deleteOneByRating(rating);
        info(id == null ? "Маршрут с таким рейтингом не найден" : "Удалён маршрут #" + id);
        results = new ArrayList<>();
    }
    public void showMax() {
        results = service.maxRating().map(List::of).orElseGet(ArrayList::new);
        if (results.isEmpty()) info("Маршрутов пока нет");
    }
    public void showLessThan() { results = service.ratingLessThan(rating); }
    public void findBetween() { results = service.findBetween(fromId, toId, sort); }
    public void addBetween() {
        Integer id = service.addBetween(name, coordinateX, coordinateY, fromId, toId, distance, newRating);
        info("Создан маршрут #" + id);
        results = service.findBetween(fromId, toId, sort);
    }
    private void info(String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, text, null));
    }
    public List<LocationDto> getLocations() { return locations; }
    public List<RouteDto> getResults() { return results; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getFromId() { return fromId; }
    public void setFromId(Integer fromId) { this.fromId = fromId; }
    public Integer getToId() { return toId; }
    public void setToId(Integer toId) { this.toId = toId; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCoordinateX() { return coordinateX; }
    public void setCoordinateX(double coordinateX) { this.coordinateX = coordinateX; }
    public long getCoordinateY() { return coordinateY; }
    public void setCoordinateY(long coordinateY) { this.coordinateY = coordinateY; }
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    public Integer getNewRating() { return newRating; }
    public void setNewRating(Integer newRating) { this.newRating = newRating; }
}
