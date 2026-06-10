package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Set;

/**
 * Контроллер панели администратора.
 * Обрабатывает запросы на отображение списка пользователей, создание,
 * редактирование, удаление пользователей и назначение ролей.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/new";
    }

    @PostMapping
    public String create(@ModelAttribute User user,
                         @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        userService.create(user, roleIds != null ? roleIds : Set.of());
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute User user,
                         @RequestParam(value = "roleIds", required = false) Set<Long> roleIds,
                         @RequestParam(value = "newPassword", required = false) String newPassword) {
        userService.update(user, roleIds != null ? roleIds : Set.of(), newPassword);
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
