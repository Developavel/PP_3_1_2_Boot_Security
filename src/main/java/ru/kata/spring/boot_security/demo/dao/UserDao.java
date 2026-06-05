package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserDao {

    List<User> listUsers();

    User show(int id);

    User findByUsername(String username);

    User findById(int id);

    void save(User user);

    void update(User user);

    void delete(int id);
}
