package com.ttn.practice.service;

import com.ttn.practice.model.User;

import java.util.Optional;

public interface UserService {
    boolean existByUsername(String username);
    void save(User user);
    User getCurrentUser();
}
