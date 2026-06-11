package ru.kata.spring.boot_security.demo.services;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * Содержит бизнес-логику: создание, обновление, удаление пользователей,
 * назначение ролей, кодирование пароля, проверку наличия ролей при обновлении.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    @Transactional
    public void create(User user, Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            user.setRoles(Set.of(roleService.getDefaultRole()));
        } else {
            user.setRoles(convertIdsToRoles(roleIds));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    @Transactional
    public void update(User user, Set<Long> roleIds, String newPassword) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен иметь как минимум одну роль!");
        }
        User existing = findById(user.getId()).orElseThrow(() -> new RuntimeException("Пользователь не найден!"));
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setAge(user.getAge());
        existing.setEmail(user.getEmail());

        if (newPassword != null && !newPassword.isEmpty()) {
            existing.setPassword(passwordEncoder.encode(newPassword));
        }
        existing.setRoles(convertIdsToRoles(roleIds));
        userDao.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userDao.delete(id);
    }

    private Set<Role> convertIdsToRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return roleIds.stream()
                .map(roleService::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }
}
