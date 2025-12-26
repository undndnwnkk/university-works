package com.web.weblab4.controller;

import com.web.weblab4.model.Point;
import com.web.weblab4.model.User;
import com.web.weblab4.repository.PointRepository;
import com.web.weblab4.repository.UserRepository;
import com.web.weblab4.service.AreaService;
import com.web.weblab4.util.Secured;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/points")
@Secured
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointController {
    @Inject
    private UserRepository userRepository;

    @Inject
    private PointRepository pointRepository;

    @Inject
    private AreaService areaChecker;

    @Context
    private SecurityContext securityContext;

    @POST
    public Response addPoint(@Valid Point point) {
        long startTime = System.nanoTime();

        String username = securityContext.getUserPrincipal().getName();
        User owner = userRepository.findByUsername(username);

        point.setOwner(owner);
        point.setHit(areaChecker.isHit(point.getX(), point.getY(), point.getR()));

        point.setExecutionTime((System.nanoTime() - startTime) / 1000);

        pointRepository.save(point);
        return Response.ok(point).build();
    }

    @GET
    public List<Point> getPoints() {
        String username = securityContext.getUserPrincipal().getName();
        User owner = userRepository.findByUsername(username);
        return pointRepository.findByUserId(owner.getId());
    }
}
