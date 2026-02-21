package com.web.weblab4.controller;

import com.web.weblab4.dto.PointDto;
import com.web.weblab4.model.Point;
import com.web.weblab4.model.User;
import com.web.weblab4.repository.PointRepository;
import com.web.weblab4.repository.UserRepository;
import com.web.weblab4.service.AreaService;
import com.web.weblab4.service.PointService;
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
    private PointService pointService;

    @POST
    public Response addPoint(PointDto point) {

        Point response = pointService.addPoint(point);
        return Response.ok(response).build();
    }

    @GET
    public Response getPoints() {
        List<Point> result = pointService.getAllPoints();
        return Response.ok(result).build();
    }
}
