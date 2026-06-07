package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Override
    public Optional<User> findById(int id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional
    public void create(User user, Set<Integer> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(convertIdsToRoles(roleIds));
        userDao.save(user);
    }

    @Override
    @Transactional
    public void update(User user, Set<Integer> roleIds, String newPassword) {
        User existing = findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUsername(user.getUsername());
        existing.setLastname(user.getLastname());
        if (newPassword != null && !newPassword.isEmpty()) {
            existing.setPassword(passwordEncoder.encode(newPassword));
        }
        existing.setRoles(convertIdsToRoles(roleIds));
        userDao.save(existing);   // ← вместо update
    }

    @Override
    @Transactional
    public void delete(int id) {
        userDao.delete(id);
    }

    private Set<Role> convertIdsToRoles(Set<Integer> roleIds) {
        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            for (Integer id : roleIds) {
                roleDao.findById(id).ifPresent(roles::add);
            }
        }
        return roles;
    }
}
