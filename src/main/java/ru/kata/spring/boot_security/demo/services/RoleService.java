package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Сервис ролей.
 * Используется для получения списка всех ролей (например, для отображения в формах),
 * а также для поиска роли по имени или ID. Метод getDefaultRole возвращает роль,
 * которая назначается новым пользователям, если они не выбрали ни одной роли.
 */
public interface RoleService {
    List<Role> getAllRoles();

    Optional<Role> findByName(String name);

    Optional<Role> findById(Long id);

    Role getDefaultRole();
}
