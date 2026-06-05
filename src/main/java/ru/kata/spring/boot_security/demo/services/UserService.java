package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
 List<User> listUsers();
 Optional<User> findByUsername(String username);
 Optional<User> findById(int id);
 void create(User user, Set<Integer> roleIds);
 void update(int id, String username, String lastname, String newPassword, Set<Integer> roleIds);
 void delete(int id);
}
