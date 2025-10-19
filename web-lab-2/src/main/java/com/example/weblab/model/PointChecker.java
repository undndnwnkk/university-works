package com.example.weblab.model;

public class PointChecker {
    public static boolean checkHit(double x, double y, double r) {
        if (x > 0 && y > 0) {
            return (x <= r / 2) && (y <= r / 2) && (x*x + y*y <= (r/2) * (r/2));
        } else if(x < 0 && y < 0) {
            return (x >= -r/2) && (y >= -r) && (y >= -2 * x - r);
        } else if (x > 0 && y < 0) {
            return (x <= r) && (y >= -r);
        } else {
            return false;
        }
    }
}
