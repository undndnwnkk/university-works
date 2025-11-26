package com.example.weblab.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class NavigationBean {
    public String goToMain() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isLoggedIn", true);

        return "main?faces-redirect=true";
    }
}
