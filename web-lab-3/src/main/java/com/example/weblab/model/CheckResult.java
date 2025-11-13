package com.example.weblab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x_value")
    private double x;

    @Column(name = "y_value")
    private double y;

    @Column(name = "r_value")
    private double r;

    @Column(name = "is_hit")
    private boolean hitResult;

    @Column(name = "created_at")
    private LocalDateTime checkTimestamp;

    @Column(name = "elapsed_ms")
    private long executionTime;
}
