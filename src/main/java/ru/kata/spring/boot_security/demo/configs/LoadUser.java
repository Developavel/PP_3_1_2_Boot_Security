package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Set;

@Component
public class LoadUser {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public LoadUser(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void loadUsers() {
        if (roleDao.listRoles().isEmpty()) {
            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");

            roleDao.save(roleUser);
            roleDao.save(roleAdmin);

            User user = new User("user", "Userov", passwordEncoder.encode("user"), Set.of(roleUser));
            User admin = new User("admin", "Adminov", passwordEncoder.encode("admin"), Set.of(roleAdmin, roleUser));

            userDao.save(user);
            userDao.save(admin);

            System.out.println("Тестовые пользователи созданы: user/user, admin/admin");
        } else {
            System.out.println("Пользователи уже существуют, инициализация пропущена.");
        }
    }
}