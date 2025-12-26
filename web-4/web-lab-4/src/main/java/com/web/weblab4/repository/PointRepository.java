package com.web.weblab4.repository;

import com.web.weblab4.model.Point;

import java.util.List;
import java.util.UUID;

public interface PointRepository {
    void save(Point point);
    List<Point> findByUserId(UUID userId);
    List<Point> findAll();
}
