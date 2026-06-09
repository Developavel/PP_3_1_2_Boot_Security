package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * DAO-интерфейс для доступа к данным ролей.
 * Определяет методы для получения списка всех ролей, поиска по идентификатору или имени,
 * а также для сохранения роли.
 * Методы поиска возвращают {@link Optional}, что позволяет безопасно обрабатывать отсутствие роли.
 */
public interface RoleDao {
    List<Role> listRoles();

    Optional<Role> findById(int id);

    Optional<Role> findByName(String name);

    void save(Role role);
}
