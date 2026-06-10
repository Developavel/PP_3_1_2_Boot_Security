package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для сущности {@link Role}.
 * Использует {@link EntityManager} для выполнения запросов к базе данных.
 * Метод {@link #findById} – ищет роль по её идентификатору и возвращает {@link Optional}.
 * Метод {@link #findByName} – ищет роль по имени и возвращает Optional, обрабатывает NoResultException.
 * Метод {@link #save} поддерживает как вставку новых ролей (persist), так и обновление существующих (merge).
 */
@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> listRoles() {
        return entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Role.class, id));
    }

    @Override
    public void save(Role role) {
        if (role.getId() == null) {
            entityManager.persist(role);
        } else {
            entityManager.merge(role);
        }
    }

    @Override
    public Optional<Role> findByName(String name) {
        try {
            return Optional.of(entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
