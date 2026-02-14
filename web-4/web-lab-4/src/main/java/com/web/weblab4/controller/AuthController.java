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

    @Inject
    private UserRepository userRepository;

    @POST
    @Path("/register")
    public Response register(User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("error", "Username or password is empty"))
                    .build();
        }


        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "User already exists"))
                    .build();
        }

        if (user.getPassword().length() < 8) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Password must be at least 8 symbols"))
                    .build();
        }

        authService.register(user.getUsername(), user.getPassword());

        String token = authService.generateToken(user.getUsername());

        return Response.ok(Map.of("token", token)).build();
    }

    @POST
    @Path("/login")
    public Response login(User toLogin) {
        User existingUser = userRepository.findByUsername(toLogin.getUsername());

        if (existingUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "incorrect login"))
                    .build();
        }

        if (authService.checkPassword(toLogin.getPassword(), existingUser.getPassword())) {
            String token = authService.generateToken(toLogin.getUsername());
            return Response.ok(Collections.singletonMap("token", token)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Incorrect password"))
                    .build();
        }
    }
}
