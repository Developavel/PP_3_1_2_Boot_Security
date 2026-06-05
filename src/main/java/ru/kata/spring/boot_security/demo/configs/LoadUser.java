package ru.kata.spring.boot_security.demo.configs;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
    private static final Logger log = LoggerFactory.getLogger(LoadUser.class);

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
            Role userRole = new Role("ROLE_USER");
            Role adminRole = new Role("ROLE_ADMIN");

            roleDao.save(userRole);
            roleDao.save(adminRole);

            User user = new User("User", "Userov",
                    passwordEncoder.encode("user"), Set.of(userRole));
            User admin = new User("Admin", "Adminov",
                    passwordEncoder.encode("admin"), Set.of(adminRole, userRole));

            userDao.save(user);
            userDao.save(admin);

            log.info("Тестовые пользователи созданы: user/user, admin/admin");
        } else {
            log.debug("Пользователи уже существуют, инициализация пропущена.");
        }
    }
}
