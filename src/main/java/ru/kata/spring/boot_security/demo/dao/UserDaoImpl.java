package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

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
                        "SELECT DISTINCT u FROM User u JOIN FETCH u.roles", User.class)
                .getResultList();
    }

    @Override
    public User show(int id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            user.getRoles().size();  // принудительная загрузка ролей
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        try {
            return entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findById(int id) {
        return entityManager.find(User.class, id);
    }

//    @Override
//    @Transactional
//    public void save(User user) {
//        entityManager.persist(user);
//    }

    @Override
    @Transactional
    public void save(User user) {
        System.out.println("=== UserDaoImpl.save ===");
        System.out.println("User: " + user.getUsername());
        System.out.println("Roles: " + user.getRoles());
        System.out.println("Roles size: " + (user.getRoles() != null ? user.getRoles().size() : "null"));
        entityManager.persist(user);
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    public void delete(int id) {
        User user = show(id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}