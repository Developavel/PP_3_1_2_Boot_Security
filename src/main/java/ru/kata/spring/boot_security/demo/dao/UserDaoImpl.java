package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для сущности {@link User}.
 * Использует {@link EntityManager} для выполнения запросов к базе данных.
 * Методы поиска ({@link #findById}, {@link #findByEmail}) используют JOIN FETCH
 * для загрузки ролей в одной транзакции и возвращают {@link Optional}.
 * Метод {@link #save}: если id == null, тогда persist(добавление новой записи) иначе merge (обновление).
 * Метод {@link #delete} сначала находит пользователя, затем удаляет.
 */
@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> listUsers() {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u JOIN FETCH u.roles ORDER BY u.id ASC", User.class)
                .getResultList();
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User user = entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
