package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления ролями.
 */
public interface RoleService {

    /**
     * Возвращает список всех ролей.
     *
     * @return список ролей
     */
    List<Role> getAllRoles();

    /**
     * Находит роль по ID.
     *
     * @param id идентификатор роли
     * @return Optional с найденной ролью
     */
    Optional<Role> findById(Long id);

    /**
     * Возвращает роль по умолчанию (ROLE_USER).
     *
     * @return роль пользователя
     */
    Role getDefaultRole();
}
