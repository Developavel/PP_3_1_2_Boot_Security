package ru.kata.spring.boot_security.demo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * Использует Spring Data JPA.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> listUsers() {
        return userRepository.findAllWithRoles();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findByIdWithRoles(id);
    }

    @Override
    @Transactional
    public void create(User user, Set<Long> roleIds) {
        log.info("Creating user: {}", user.getEmail());

        // Проверка на дубликат email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email '" + user.getEmail() + "' уже существует!");
        }

        // Назначение ролей
        if (roleIds == null || roleIds.isEmpty()) {
            user.setRoles(Set.of(roleService.getDefaultRole()));
        } else {
            user.setRoles(convertIdsToRoles(roleIds));
        }

        // Кодирование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        log.info("User created successfully: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void update(User user, Set<Long> roleIds, String newPassword) {
        log.info("Updating user with ID: {}", user.getId());

        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен иметь как минимум одну роль!");
        }

        User existing = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден! ID: " + user.getId()));

        // Проверка на дубликат email (если email изменен)
        if (!existing.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email '" + user.getEmail() + "' уже существует!");
        }

        // Обновление полей
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setAge(user.getAge());
        existing.setEmail(user.getEmail());

        // Обновление пароля (если указан)
        if (newPassword != null && !newPassword.isEmpty()) {
            existing.setPassword(passwordEncoder.encode(newPassword));
        }

        // Обновление ролей
        existing.setRoles(convertIdsToRoles(roleIds));

        userRepository.save(existing);
        log.info("User updated successfully: {}", existing.getEmail());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден. ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully: ID {}", id);
    }

    private Set<Role> convertIdsToRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return roleIds.stream()
                .map(roleService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}