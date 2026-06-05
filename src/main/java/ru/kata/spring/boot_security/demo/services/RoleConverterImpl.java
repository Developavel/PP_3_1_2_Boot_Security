package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.models.Role;
import java.util.HashSet;
import java.util.Set;

@Component
public class RoleConverterImpl implements RoleConverter {
    private final RoleDao roleDao;

    public RoleConverterImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Set<Role> convert(Set<Integer> roleIds) {
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            for (Integer roleId : roleIds) {
                Role role = roleDao.findById(roleId);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }
}
