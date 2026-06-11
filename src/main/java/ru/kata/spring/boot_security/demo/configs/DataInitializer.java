package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Set;

/**
 * Инициализация начальных данных: роли ROLE_USER, ROLE_ADMIN и учётная запись администратора.
 * Данные администратора загружаются из application.yml.
 * Инициализация выполняется только при отсутствии соответствующих записей в БД.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;

    @Value("${app.admin.age}")
    private int adminAge;

    @Override
    @Transactional
    public void run(String... args) {
        Role roleUser = findOrCreateRole("ROLE_USER");
        Role roleAdmin = findOrCreateRole("ROLE_ADMIN");

        if (userDao.findByEmail(adminEmail).isEmpty()) {
            User admin = new User(
                    adminFirstName,
                    adminLastName,
                    adminAge,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    Set.of(roleAdmin, roleUser));
            userDao.save(admin);
            log.info("Создан администратор с email: {}", adminEmail);
        }

        log.info("Инициализация завершена");
    }

    private Role findOrCreateRole(String roleName) {
        return roleDao.findByName(roleName).orElseGet(() -> {
            Role role = new Role(roleName);
            roleDao.save(role);
            log.info("Создана роль {}", roleName);
            return role;
        });
    }
}
