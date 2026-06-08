package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    List<Role> listRoles();

    Optional<Role> findById(int id);

    Optional<Role> findByName(String name);

    void save(Role role);
}
