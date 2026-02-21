package org.example;

public class HitChecker implements Command{
    private final Request request;
    private boolean isHitted = false;
    public HitChecker(Request request){
        this.request = request;
    }

    @Override
    public void execute() {
        double x = request.getX();
        double y = request.getY();
        double r = request.getR();

        if (x >= 0 && y >= 0) {
            if (x * x + y * y <= r * r) isHitted = true;
        }

        if (x < 0 && y >= 0) {
            if (y <= x + r / 2) isHitted = true;
        }

        if (x < 0 && y < 0) {
            if (x >= -r && y >= -r / 2) isHitted = true;
        }
    }

    public boolean getHitted() {
        return isHitted;
    }
}
