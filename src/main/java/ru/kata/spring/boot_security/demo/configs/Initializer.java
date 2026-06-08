package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Set;

@Component
public class Initializer {

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
        Role roleUser = roleDao.findByName("ROLE_USER");
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleDao.save(roleUser);
            System.out.println("Создана роль ROLE_USER");
        }

        Role roleAdmin = roleDao.findByName("ROLE_ADMIN");
        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleDao.save(roleAdmin);
            System.out.println("Создана роль ROLE_ADMIN");
        }

        // Создаём администратора, если его нет
        if (userDao.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "Adminov", passwordEncoder.encode("admin"), Set.of(roleAdmin, roleUser));
            userDao.save(admin);
            System.out.println("Создан пользователь admin/admin");
        }

        System.out.println("Инициализация завершена");
    }
}
