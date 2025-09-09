package org.example;

public record RequestEntity(
        int number,
        String time,
        double x,
        double y,
        double r,
        boolean hit,
        String elapsedMs
) {
}
