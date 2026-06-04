package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class LoadUser {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoadUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void loadUsers() {
        if (roleRepository.count() == 0) {
            Role user = new Role("ROLE_USER");
            Role admin = new Role("ROLE_ADMIN");

            roleRepository.save(user);
            roleRepository.save(admin);

            User userUser = new User("user", "lastname1",
                    passwordEncoder.encode("user"), Set.of(user));
            User adminUser = new User("admin", "lastname2",
                    passwordEncoder.encode("admin"), Set.of(admin, user));

            userRepository.save(userUser);
            userRepository.save(adminUser);

            System.out.println("Пользователи созданы");
        }
    }
}