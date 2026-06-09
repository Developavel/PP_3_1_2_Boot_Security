package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Set;

/**
 * Компонент для начальной инициализации данных.
 * При старте приложения создаёт роли ROLE_USER и ROLE_ADMIN (если их нет),
 * а также администратора (admin/admin), если он отсутствует.
 */
@Component
public class Initializer {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public Initializer(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void loadUsers() {
        // Создаём роли, если их нет
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

        // Создаём администратора, если его нет
        if (userDao.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "Adminov", passwordEncoder.encode("admin"), Set.of(roleAdmin, roleUser));
            userDao.save(admin);
            log.info("Создан пользователь admin/admin");
        }

        log.info("Инициализация завершена");
    }
}
