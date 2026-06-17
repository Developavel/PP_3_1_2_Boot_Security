package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для сущности {@link User}.
 * Использует {@link EntityManager} для выполнения запросов к базе данных.
 * Методы поиска ({@link #findById}, {@link #findByEmail}) используют JOIN FETCH
 * для загрузки ролей в одной транзакции и возвращают {@link Optional}.
 */
@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> listUsers() {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u JOIN FETCH u.roles ORDER BY u.id ASC",
                        User.class)
                .getResultList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return getSingleResult(
                entityManager.createQuery(
                                "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.id = :id",
                                User.class)
                        .setParameter("id", id)
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return getSingleResult(
                entityManager.createQuery(
                                "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.email = :email",
                                User.class)
                        .setParameter("email", email)
        );
    }

    @Override
    @Transactional
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Используем getReference() для избежания лишнего SELECT
        // Если пользователь не существует, будет выброшено EntityNotFoundException
        User user = entityManager.getReference(User.class, id);
        entityManager.remove(user);
    }

    /**
     * Вспомогательный метод для обработки запросов с одним результатом
     * return Optional.empty() если результат не найден
     */
    private <T> Optional<T> getSingleResult(TypedQuery<T> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}