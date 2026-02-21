package com.example.weblab.service;

import com.example.weblab.dto.CheckResultDto;
import com.example.weblab.dto.CoordinatesDto;
import com.example.weblab.dto.Mapper;
import com.example.weblab.model.CheckResult;
import com.example.weblab.repository.CheckResultRepository;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AreaCheckService {
    @Inject
    private CheckResultRepository checkResultRepository;
    private static final Counter pointsCounter = Counter.build()
            .name("points_total")
            .help("Total number of checked points")
            .labelNames("result")
            .register();

    private static final Histogram requestLatency = Histogram.build()
            .name("check_durations")
            .help("Time taken to check a point in seconds")
            .register();

    @Transactional
    public CheckResultDto checkPoint(CoordinatesDto input) {
        Histogram.Timer requestTimer = requestLatency.startTimer();
        long startTime = System.nanoTime();
        boolean isHit = isHit(input.getX(), input.getY(), input.getR());
        long executionTime = System.nanoTime() - startTime;

        CheckResult entity = Mapper.toEntity(input, isHit, executionTime);

        checkResultRepository.save(entity);

        if (isHit) pointsCounter.labels("hit").inc();
        else pointsCounter.labels("miss").inc();
        requestTimer.observeDuration();

        return Mapper.toDto(entity);
    }

    public List<CheckResultDto> getAllResults() {
        return checkResultRepository.findAll().stream()
                .map(Mapper::toDto).collect(Collectors.toList());
    }

    private boolean isHit(Double x, Double y, Double r) {
        boolean isHit = false;

        if (x <= 0 && y >= 0) {
            isHit = (y <= (x / 2 + r / 2));
        }
        else if (x <= 0 && y <= 0) {
            isHit = (x >= -r) && (y >= -r);
        }
        else if (x >= 0 && y <= 0) {
            isHit = (x * x + y * y <= (r / 2) * (r / 2));
        }
        return isHit;
    }
}
