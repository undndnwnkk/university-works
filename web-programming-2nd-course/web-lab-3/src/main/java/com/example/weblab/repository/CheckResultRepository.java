package com.example.weblab.repository;

import com.example.weblab.model.CheckResult;

import java.util.List;

public interface CheckResultRepository {
    void save(CheckResult checkResult);
    List<CheckResult> findAll();
}
