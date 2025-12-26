package com.web.weblab4.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AreaService {
    public boolean isHit(double x, double y, double r) {
        if (r >= 0) {
            return checkPositiveR(x, y, r);
        } else {
            return checkNegativeR(x, y, r);
        }
    }

    private boolean checkPositiveR(double x, double y, double r) {
        if (x >= 0 && y >= 0) {
            return (x * x + y * y) <= (r / 2) * (r / 2);
        }

        if (x <= 0 && y <= 0) {
            return y >= -x - (r / 2);
        }

        if (x >= 0 && y <= 0) {
            return x <= r && y >= -r;
        }

        return false;
    }

    private boolean checkNegativeR(double x, double y, double r) {
        if (x <= 0 && y <= 0) {
            return (x * x + y * y) <= (r / 2) * (r / 2);
        }

        if (x >= 0 && y >= 0) {
            return y <= -x - (r / 2);
        }

        if (x <= 0 && y >= 0) {
            return x >= r && y <= -r;
        }

        return false;
    }
}
