package org.example;

public record ResponseEntity(
        int number,
        String time,
        double x,
        double y,
        double r,
        boolean hit,
        Long elapsedMs
) {
}
