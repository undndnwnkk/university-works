package ru.itmo.routes.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.dto.RouteDto;
import ru.itmo.routes.dto.RouteForm;
import ru.itmo.routes.service.LocationService;
import ru.itmo.routes.service.RouteService;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class RouteDetailBean implements Serializable {
    @Inject private RouteService service;
    @Inject private LocationService locationService;
    private Integer id;
    private RouteDto route;
    private RouteForm draft;
    private List<LocationDto> locations;
    private boolean editorOpen;

    public void load() {
        if (FacesContext.getCurrentInstance().isPostback()) return;
        locations = locationService.all();
        route = service.find(id).orElse(null);
        if (route == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Маршрут не найден", null));
        }
    }

    public void openEdit() {
        draft = RouteForm.of(route);
        editorOpen = true;
    }

    public void save() {
        route = service.update(id, draft);
        editorOpen = false;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Изменения сохранены", null));
    }

    public void closeEditor() { editorOpen = false; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public RouteDto getRoute() { return route; }
    public RouteForm getDraft() { return draft; }
    public List<LocationDto> getLocations() { return locations; }
    public boolean isEditorOpen() { return editorOpen; }
}
