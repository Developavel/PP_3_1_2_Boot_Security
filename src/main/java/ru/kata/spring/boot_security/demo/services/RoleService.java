package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();

    //Role findByName(String name);
    Optional<Role> findByName(String name);

    //Role findById(int id);
    Optional<Role> findById(int id);

    Role getDefaultRole();
}
