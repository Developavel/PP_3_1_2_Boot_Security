package ru.kata.spring.boot_security.demo.services;

import java.util.Set;

public class UserUpdateRequest {
    private int id;
    private String username;
    private String lastname;
    private String newPassword;
    private Set<Integer> roleIds;

    // Конструкторы, геттеры, сеттеры
    public UserUpdateRequest() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Set<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
