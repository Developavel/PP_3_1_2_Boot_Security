package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для доступа к данным пользователей.
 * Определяет методы для CRUD-операций и поиска пользователей по идентификатору или email.
 * Все методы, возвращающие Optional, позволяют безопасно обрабатывать отсутствие пользователя.
 */
public interface UserDao {
    List<User> listUsers();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    void save(User user);

    void delete(Long id);
}
