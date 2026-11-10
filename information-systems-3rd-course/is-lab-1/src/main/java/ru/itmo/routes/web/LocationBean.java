package ru.itmo.routes.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.routes.dto.LocationDto;
import ru.itmo.routes.dto.LocationForm;
import ru.itmo.routes.dto.LocationSummaryDto;
import ru.itmo.routes.service.LocationService;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class LocationBean implements Serializable {
    @Inject private LocationService service;
    private List<LocationSummaryDto> rows;
    private boolean editorOpen;
    private boolean deleteOpen;
    private Integer editingId;
    private LocationForm draft;
    private Integer deleteId;
    private long deleteRouteCount;
    private Integer replacementId;

    @PostConstruct
    public void init() { reload(); }

    public void reload() { rows = service.summaries(); }

    public void openCreate() {
        editingId = null;
        draft = new LocationForm();
        editorOpen = true;
    }

    public void openEdit(Integer id) {
        LocationDto location = service.find(id).orElse(null);
        if (location == null) {
            reload();
            message(FacesMessage.SEVERITY_ERROR, "Локация не найдена");
            return;
        }
        editingId = id;
        draft = LocationForm.of(location);
        editorOpen = true;
    }

    public void save() {
        try {
            if (editingId == null) service.create(draft);
            else service.update(editingId, draft);
            editorOpen = false;
            reload();
            message(FacesMessage.SEVERITY_INFO, editingId == null ? "Локация создана" : "Изменения сохранены");
        } catch (RuntimeException exception) {
            message(FacesMessage.SEVERITY_ERROR, rootMessage(exception));
        }
    }

    public void askDelete(Integer id) {
        deleteId = id;
        replacementId = null;
        deleteRouteCount = service.routeCount(id);
        deleteOpen = true;
    }

    public void delete() {
        try {
            int moved = service.delete(deleteId, replacementId);
            deleteOpen = false;
            reload();
            message(FacesMessage.SEVERITY_INFO, moved == 0 ? "Локация удалена"
                    : "Локация удалена, маршрутов перепривязано: " + moved);
        } catch (RuntimeException exception) {
            message(FacesMessage.SEVERITY_ERROR, rootMessage(exception));
        }
    }

    public void closeEditor() { editorOpen = false; }
    public void closeDelete() { deleteOpen = false; }

    public List<LocationDto> getReplacementCandidates() {
        return rows.stream().map(LocationSummaryDto::getLocation)
                .filter(location -> !location.getId().equals(deleteId)).toList();
    }

    private void message(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) current = current.getCause();
        String text = current.getMessage();
        if (text == null) return "Операция не выполнена";
        return text.lines().findFirst().orElse(text).replaceFirst("^(ERROR|ОШИБКА):\\s*", "");
    }

    public List<LocationSummaryDto> getRows() { return rows; }
    public boolean isEditorOpen() { return editorOpen; }
    public boolean isDeleteOpen() { return deleteOpen; }
    public Integer getEditingId() { return editingId; }
    public LocationForm getDraft() { return draft; }
    public Integer getDeleteId() { return deleteId; }
    public long getDeleteRouteCount() { return deleteRouteCount; }
    public Integer getReplacementId() { return replacementId; }
    public void setReplacementId(Integer replacementId) { this.replacementId = replacementId; }
}
