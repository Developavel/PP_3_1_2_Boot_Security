package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> listUsers();

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    void save(User user);

    void delete(int id);
}
