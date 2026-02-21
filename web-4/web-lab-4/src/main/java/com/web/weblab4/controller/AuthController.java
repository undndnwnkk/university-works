package com.web.weblab4.controller;

import com.web.weblab4.model.User;
import com.web.weblab4.repository.UserRepository;
import com.web.weblab4.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    @Inject
    private AuthService authService;

    @POST
    @Path("/register")
    public Response register(User user) {
        Map<String, String> response = authService.register(user);
        return Response.ok(response).build();
    }

    @POST
    @Path("/login")
    public Response login(User toLogin) {
        Map<String, String> response = authService.login(toLogin);
        return Response.ok(response).build();
    }
}
