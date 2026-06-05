package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.models.Role;
import java.util.List;

public interface RoleDao {
    List<Role> listRoles();
    Role findById(int id);
    void save(Role role);
}
