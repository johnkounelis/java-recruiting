package com.recruiting.dao;

import com.recruiting.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    List<User> findAll();
    int create(User user);
    boolean update(User user);
    boolean delete(int id);
}