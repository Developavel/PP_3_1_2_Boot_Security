package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для управления пользователями.
 */
public interface UserService {
    List<User> listUsers();

    Optional<User> findById(Long id);

    void create(User user, Set<Long> roleIds);

    void update(User user, Set<Long> roleIds, String newPassword);

    void delete(Long id);

}