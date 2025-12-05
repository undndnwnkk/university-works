package com.example.weblab.dto;

import com.example.weblab.model.CheckResult;

import java.time.LocalDateTime;

public class Mapper {
    public static CheckResultDto toDto(CheckResult entity) {
        return new CheckResultDto(
                entity.getX(),
                entity.getY(),
                entity.getR(),
                entity.isHitResult(),
                entity.getCheckTimestamp(),
                entity.getExecutionTime()
        );
    }

    public static CheckResult toEntity(CoordinatesDto coordinates, boolean result, long executionTime) {
        return new CheckResult(
                coordinates.getX(),
                coordinates.getY(),
                coordinates.getR(),
                result,
                LocalDateTime.now(),
                executionTime
        );
    }
}
