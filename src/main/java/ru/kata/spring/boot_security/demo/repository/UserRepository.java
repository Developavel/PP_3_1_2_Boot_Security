package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 * Использует Spring Data JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по email с загрузкой ролей
     */
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    /**
     * Находит всех пользователей с загрузкой ролей
     */
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles ORDER BY u.id ASC")
    List<User> findAllWithRoles();

    /**
     * Находит пользователя по ID с загрузкой ролей
     */
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    /**
     * Проверяет существование пользователя по email
     */
    boolean existsByEmail(String email);
}