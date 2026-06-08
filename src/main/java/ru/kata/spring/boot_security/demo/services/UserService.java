package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    List<User> listUsers();

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    void create(User user, Set<Integer> roleIds);

    void update(User user, Set<Integer> roleIds, String newPassword);

    void delete(int id);
}
