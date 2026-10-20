package ru.itmo.routes.web;

import jakarta.annotation.PostConstruct;
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
public class RouteListBean implements Serializable {
    @Inject private RouteService service;
    private List<Route> routes;
    private List<Location> locations;
    private List<Route> allRoutes;
    private String filter = "";
    private String sort = "creationDate";
    private boolean ascending;
    private int page;
    private int pageSize = 8;
    private long total;
    private boolean editorOpen;
    private boolean deleteOpen;
    private Integer editingId;
    private Integer fromId;
    private Integer toId;
    private Integer deleteId;
    private Integer replacementId;
    private Route draft;

    @PostConstruct
    public void init() {
        locations = service.locations();
        allRoutes = service.all();
        reload();
    }

    public void reload() {
        total = service.count(filter);
        int last = Math.max(0, getPageCount() - 1);
        if (page > last) page = last;
        routes = service.page(filter, sort, ascending, page, pageSize);
    }

    public void applyFilter() { page = 0; reload(); }
    public void nextPage() { if (page + 1 < getPageCount()) { page++; reload(); } }
    public void previousPage() { if (page > 0) { page--; reload(); } }

    public void sortBy(String field) {
        if (field.equals(sort)) ascending = !ascending;
        else { sort = field; ascending = true; }
        page = 0;
        reload();
    }

    public void openCreate() {
        editingId = null;
        draft = blankRoute();
        fromId = locations.isEmpty() ? null : locations.get(0).getId();
        toId = null;
        editorOpen = true;
    }

    public void openEdit(Integer id) {
        Route route = service.find(id).orElseThrow(() -> new IllegalArgumentException("Маршрут не найден"));
        editingId = id;
        draft = copy(route);
        fromId = route.getFrom().getId();
        toId = route.getTo() == null ? null : route.getTo().getId();
        editorOpen = true;
    }

    public void save() {
        try {
            if (editingId == null) service.create(draft, fromId, toId);
            else service.update(editingId, draft, fromId, toId);
            editorOpen = false;
            allRoutes = service.all();
            reload();
            message(FacesMessage.SEVERITY_INFO, editingId == null ? "Маршрут создан" : "Изменения сохранены");
        } catch (RuntimeException exception) {
            message(FacesMessage.SEVERITY_ERROR, rootMessage(exception));
        }
    }

    public void askDelete(Integer id) {
        deleteId = id;
        replacementId = null;
        deleteOpen = true;
    }

    public void delete() {
        try {
            service.delete(deleteId, replacementId);
            deleteOpen = false;
            allRoutes = service.all();
            reload();
            message(FacesMessage.SEVERITY_INFO, "Маршрут удалён");
        } catch (RuntimeException exception) {
            message(FacesMessage.SEVERITY_ERROR, rootMessage(exception));
        }
    }

    public void closeEditor() { editorOpen = false; }
    public void closeDelete() { deleteOpen = false; }

    public int getPageCount() { return Math.max(1, (int) Math.ceil((double) total / pageSize)); }
    public long getFirstRowNumber() { return total == 0 ? 0 : (long) page * pageSize + 1; }
    public long getLastRowNumber() { return Math.min(total, (long) (page + 1) * pageSize); }

    private Route blankRoute() {
        Route route = new Route();
        route.setCoordinates(new Coordinates(0.0, 0L));
        route.setDistance(1.1f);
        route.setRating(1);
        return route;
    }

    private Route copy(Route source) {
        Route route = new Route();
        route.setName(source.getName());
        route.setCoordinates(new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY()));
        route.setDistance(source.getDistance());
        route.setRating(source.getRating());
        return route;
    }

    private void message(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) current = current.getCause();
        return current.getMessage() == null ? "Операция не выполнена" : current.getMessage();
    }

    public List<Route> getRoutes() { return routes; }
    public List<Location> getLocations() { return locations; }
    public List<Route> getAllRoutes() { return allRoutes; }
    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }
    public String getSort() { return sort; }
    public boolean isAscending() { return ascending; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; this.page = 0; reload(); }
    public long getTotal() { return total; }
    public boolean isEditorOpen() { return editorOpen; }
    public boolean isDeleteOpen() { return deleteOpen; }
    public Integer getEditingId() { return editingId; }
    public Integer getFromId() { return fromId; }
    public void setFromId(Integer fromId) { this.fromId = fromId; }
    public Integer getToId() { return toId; }
    public void setToId(Integer toId) { this.toId = toId; }
    public Integer getDeleteId() { return deleteId; }
    public Integer getReplacementId() { return replacementId; }
    public void setReplacementId(Integer replacementId) { this.replacementId = replacementId; }
    public Route getDraft() { return draft; }
}
