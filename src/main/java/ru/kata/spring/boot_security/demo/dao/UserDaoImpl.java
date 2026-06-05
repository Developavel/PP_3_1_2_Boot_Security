package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<User> listUsers() {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u JOIN FETCH u.roles ORDER BY u.id ASC", User.class)
                .getResultList();
    }

    @Override
    public Optional<User> findById(int id) {
        try {
            User user = entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            User user = entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        entityManager.persist(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void delete(int id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
