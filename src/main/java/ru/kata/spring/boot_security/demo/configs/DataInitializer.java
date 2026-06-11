package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Компонент для начальной инициализации данных.
 * При старте приложения создаёт роли ROLE_USER и ROLE_ADMIN (если их нет),
 * а также администратора email admin@mail.ru, если он отсутствует.
 */
@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        Role roleUser = roleDao.findByName("ROLE_USER").orElse(null);
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleDao.save(roleUser);
            log.info("Создана роль ROLE_USER");
        }

        Role roleAdmin = roleDao.findByName("ROLE_ADMIN").orElse(null);
        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleDao.save(roleAdmin);
            log.info("Создана роль ROLE_ADMIN");
        }

        String adminEmail = "admin@mail.ru";
        if (userDao.findByEmail(adminEmail).isEmpty()) {
            User admin = new User(
                    "Admin",
                    "Adminov",
                    36,
                    adminEmail,
                    passwordEncoder.encode("admin"),
                    Set.of(roleAdmin, roleUser));
            userDao.save(admin);
            log.info("Создан администратор с email: {}", adminEmail);
        }

        log.info("Инициализация завершена");
    }
}
