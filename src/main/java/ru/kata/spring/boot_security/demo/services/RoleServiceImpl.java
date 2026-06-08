package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.listRoles();
    }

//    @Override
//    public Role findByName(String name) {
//        return roleDao.findByName(name);
//    }

//    @Override
//    public Role findById(int id) {
//        return roleDao.findById(id).orElse(null);
//    }

//    @Override
//    public Role getDefaultRole() {
//        return findByName("ROLE_USER");
//    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleDao.findByName(name);
    }

    @Override
    public Optional<Role> findById(int id) {
        return roleDao.findById(id);
    }

    @Override
    public Role getDefaultRole() {
        return findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
    }
}
