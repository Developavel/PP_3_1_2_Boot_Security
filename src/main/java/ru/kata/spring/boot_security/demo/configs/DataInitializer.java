package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Set;

/**
 * Инициализирует роли и администратора при первом запуске.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

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
        log.info("Starting data initialization...");

        Role roleUser = findOrCreateRole("ROLE_USER");
        Role roleAdmin = findOrCreateRole("ROLE_ADMIN");

        if (shouldCreateAdmin()) {
            createAdmin(roleAdmin, roleUser);
        }

        log.info("Data initialization completed.");
    }

    private boolean shouldCreateAdmin() {
        return "create".equalsIgnoreCase(ddlAuto) || "create-drop".equalsIgnoreCase(ddlAuto);
    }

    private void createAdmin(Role roleAdmin, Role roleUser) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin already exists: {}", adminEmail);
            return;
        }

        User admin = new User(
                adminFirstName,
                adminLastName,
                adminAge,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                Set.of(roleAdmin, roleUser)
        );

        userRepository.save(admin);
        log.info("Admin created: {} (ddl-auto={})", adminEmail, ddlAuto);
    }

    private Role findOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role(roleName);
                    roleRepository.save(role);
                    log.info("Role created: {}", roleName);
                    return role;
                });
    }
}
