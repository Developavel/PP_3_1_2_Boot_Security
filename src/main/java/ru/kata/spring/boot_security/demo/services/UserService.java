package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService {

    List<User> listUsers();
     User findByUsername(String username);
     User show(int id);
     void create(User user);
     void update(User updateUser);
     void delete(int id);
}
