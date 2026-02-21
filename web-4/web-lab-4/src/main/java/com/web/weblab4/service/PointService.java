package com.web.weblab4.service;

import com.web.weblab4.dto.PointDto;
import com.web.weblab4.model.Point;
import com.web.weblab4.model.User;
import com.web.weblab4.repository.PointRepository;
import com.web.weblab4.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.Map;

@Stateless
public class PointService {
    @Inject
    private AreaService areaService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PointRepository pointRepository;

    @Context
    private SecurityContext securityContext;

    public Point addPoint(PointDto pointDto) {
        Point point = new Point();
        long startTime = System.nanoTime();

        String username = securityContext.getUserPrincipal().getName();
        User owner = userRepository.findByUsername(username);

        if (!checkConstraints(pointDto.x(), pointDto.y(), pointDto.r())) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Значения не входят в заданные границы")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }
        point.setX(pointDto.x());
        point.setY(pointDto.y());
        point.setR(pointDto.r());
        point.setOwner(owner);

        point.setHit(areaService.isHit(pointDto.x(), pointDto.y(), pointDto.r()));

        point.setExecutionTime((System.nanoTime() - startTime) / 1000);

        try {
            pointRepository.save(point);
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Ошибка сохранения точки в бд")
                            .type(MediaType.TEXT_PLAIN)
                            .build()
            );
        }

        return point;
    }

    public List<Point> getAllPoints() {
        String username = securityContext.getUserPrincipal().getName();
        User owner = userRepository.findByUsername(username);
        return pointRepository.findByUserId(owner.getId());
    }

    private boolean checkConstraints(double x, double y, double r) {
        if (r == 0) return false;
        if (r > 0) {
            if (x >= 0 && y >= 0) return (x * x + y * y) <= (r / 2) * (r / 2);
            if (x <= 0 && y <= 0) return y >= -x - (r / 2);
            if (x >= 0 && y <= 0) return x <= r && y >= -r;
        } else {
            var ar = Math.abs(r);
            if (x <= 0 && y <= 0) return (x * x + y * y) <= (ar / 2) * (ar / 2);
            if (x >= 0 && y >= 0) return y <= -x + (ar / 2);
            if (x <= 0 && y >= 0) return x >= -ar && y <= ar;
        }
        return false;
    }
}
