package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Collections;
import java.util.Set;

/**
 * Контроллер панели администратора.
 * Обрабатывает CRUD-операции для пользователей.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String REDIRECT_ADMIN = "redirect:/admin";
    private static final String ADMIN_PAGE = "admin";

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return ADMIN_PAGE;
    }

    @PostMapping
    public String create(@ModelAttribute("newUser") User user,
                         @RequestParam(required = false) Set<Long> roleIds) {
        userService.create(user, safeRoles(roleIds));
        return REDIRECT_ADMIN;
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute User user,
                         @RequestParam(required = false) Set<Long> roleIds,
                         @RequestParam(required = false) String newPassword) {
        userService.update(user, safeRoles(roleIds), newPassword);
        return REDIRECT_ADMIN;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        userService.delete(id);
        return REDIRECT_ADMIN;
    }

    private Set<Long> safeRoles(Set<Long> roleIds) {
        return roleIds == null ? Collections.emptySet() : roleIds;
    }
}