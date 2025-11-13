package com.example.weblab.beans;

import com.example.weblab.model.CheckResult;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import javax.faces.view.ViewScoped;
import java.time.LocalDateTime;

@Named
@ViewScoped
@Data
public class PointBean {
    private Double x;
    private Double y;
    private Double r;

    @Inject
    private ResultsManagerBean resultsManagerBean;

    public void checkHit() {
        long startTime = System.nanoTime();
        boolean isHit = true;
        if (x > 0 && y > 0) {
            isHit = (x <= r / 2) && (y <= r / 2) && (x*x + y*y <= (r/2) * (r/2));
        } else if(x < 0 && y < 0) {
            isHit = (x >= -r) && (y >= -r) && (y >= -2 * x - r);
        } else if (x > 0 && y < 0) {
            isHit = (x <= r) && (y >= -r);
        } else {
            isHit = false;
        }
        long executionTime = System.nanoTime() - startTime;

        CheckResult checkResult = CheckResult.builder()
                .x(this.x)
                .y(this.y)
                .r(this.r)
                .hitResult(isHit)
                .executionTime(executionTime)
                .checkTimestamp(LocalDateTime.now())
                .build();

        resultsManagerBean.addResult(checkResult);
    }
}
