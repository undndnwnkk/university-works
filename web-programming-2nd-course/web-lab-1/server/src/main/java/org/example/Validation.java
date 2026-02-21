package org.example;

public class Validation implements Command {
    private final Request request;
    private boolean isValid = true;

    public Validation(Request request) {
        this.request = request;
    }

    @Override
    public void execute() {
        Double x = request.x;
        Double y = request.y;
        Double r = request.r;

        if (x < -3 || x > 5 || x == null) {
            isValid = false;
        }

        if (y < -5 || y > 3 || y == null) {
            isValid = false;
        }

        if (r < 1 || r > 3 || r == null) {
            isValid = false;
        }
    }

    public Request getRequest() {
        return request;
    }

    public boolean getValid() {
        return isValid;
    }
}
