package com.example.weblab.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CheckResultDto implements Serializable {
    private Double x;
    private Double y;
    private Double r;
    private boolean hitResult;
    private LocalDateTime checkTimestamp;
    private long executionTime;

    public CheckResultDto() {
    }

    public CheckResultDto(Double x, Double y, Double r, boolean hitResult, LocalDateTime checkTimestamp, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hitResult = hitResult;
        this.checkTimestamp = checkTimestamp;
        this.executionTime = executionTime;
    }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }

    public boolean isHitResult() { return hitResult; }
    public void setHitResult(boolean hitResult) { this.hitResult = hitResult; }

    public LocalDateTime getCheckTimestamp() { return checkTimestamp; }
    public void setCheckTimestamp(LocalDateTime checkTimestamp) { this.checkTimestamp = checkTimestamp; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}