package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Optional;

/**
 * Репозиторий для работы с ролями.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Находит роль по имени.
     *
     * @param name имя роли (например, ROLE_ADMIN)
     * @return Optional с найденной ролью
     */
    Optional<Role> findByName(String name);
}
