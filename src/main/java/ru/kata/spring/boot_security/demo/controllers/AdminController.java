package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

/**
 * Контроллер панели администратора.
 * Обрабатывает CRUD-операции для пользователей.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String REDIRECT_ADMIN = "redirect:/admin";

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public ModelAndView adminPage() {
        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("users", userService.listUsers());
        modelAndView.addObject("newUser", new User());
        modelAndView.addObject("allRoles", roleService.getAllRoles());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView create(@ModelAttribute("newUser") User user,
                         @RequestParam(required = false) Set<Long> roleIds) {
        userService.create(user, safeRoles(roleIds));
        return new ModelAndView(REDIRECT_ADMIN);
    }

    @PostMapping("/edit")
    public ModelAndView update(@ModelAttribute User user,
                         @RequestParam(required = false) Set<Long> roleIds,
                         @RequestParam(required = false) String newPassword) {
        userService.update(user, safeRoles(roleIds), newPassword);
        return new ModelAndView(REDIRECT_ADMIN);
    }

    @PostMapping("/delete")
    public ModelAndView delete(@RequestParam Long id) {
        userService.delete(id);
        return new ModelAndView(REDIRECT_ADMIN);
    }

    private Set<Long> safeRoles(Set<Long> roleIds) {
        return roleIds == null ? Collections.emptySet() : roleIds;
    }
}