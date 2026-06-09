package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для управления пользователями.
 * Определяет контракт для создания, обновления, удаления и поиска пользователей по идентификатору.
 * Реализации должны обеспечивать кодирование пароля и проверку наличия ролей.
 */
public interface UserService {
    List<User> listUsers();

    Optional<User> findById(int id);

    void create(User user, Set<Integer> roleIds);

    void update(User user, Set<Integer> roleIds, String newPassword);

    void delete(int id);
}
