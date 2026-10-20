package ru.itmo.routes.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.routes.domain.Coordinates;
import ru.itmo.routes.domain.Location;
import ru.itmo.routes.domain.Route;
import ru.itmo.routes.service.RouteService;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class RouteDetailBean implements Serializable {
    @Inject private RouteService service;
    private Integer id;
    private Route route;
    private Route draft;
    private List<Location> locations;
    private Integer fromId;
    private Integer toId;
    private boolean editorOpen;

    public void load() {
        if (FacesContext.getCurrentInstance().isPostback()) return;
        locations = service.locations();
        route = service.find(id).orElse(null);
        if (route == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Маршрут не найден", null));
        }
    }

    public void openEdit() {
        draft = new Route();
        draft.setName(route.getName());
        draft.setCoordinates(new Coordinates(route.getCoordinates().getX(), route.getCoordinates().getY()));
        draft.setDistance(route.getDistance());
        draft.setRating(route.getRating());
        fromId = route.getFrom().getId();
        toId = route.getTo() == null ? null : route.getTo().getId();
        editorOpen = true;
    }

    public void save() {
        route = service.update(id, draft, fromId, toId);
        editorOpen = false;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Изменения сохранены", null));
    }

    public void closeEditor() { editorOpen = false; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Route getRoute() { return route; }
    public Route getDraft() { return draft; }
    public List<Location> getLocations() { return locations; }
    public Integer getFromId() { return fromId; }
    public void setFromId(Integer fromId) { this.fromId = fromId; }
    public Integer getToId() { return toId; }
    public void setToId(Integer toId) { this.toId = toId; }
    public boolean isEditorOpen() { return editorOpen; }
}
