package com.example.weblab.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class CheckResult implements Serializable {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private String currentTime;
    private long executionTime;
}
