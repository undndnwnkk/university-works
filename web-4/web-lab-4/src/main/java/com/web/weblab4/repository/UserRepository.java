package com.web.weblab4.repository;

import com.web.weblab4.model.User;

import java.util.List;

public interface UserRepository {
    void save(User user);
    List<User> findAll();
    User findByUsername(String username);
}
