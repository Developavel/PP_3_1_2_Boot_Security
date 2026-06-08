package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> listRoles() {
        return entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    @Override
    public Optional<Role> findById(int id) {
        return Optional.ofNullable(entityManager.find(Role.class, id));
    }

    @Override
    @Transactional
    public void save(Role role) {
        if (role.getId() == 0) {
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
