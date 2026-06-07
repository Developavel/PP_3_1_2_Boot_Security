package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import java.util.Set;

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
        return "admin/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.listRoles());
        return "admin/new";
    }

    @PostMapping
    public String create(@ModelAttribute User user,
                         @RequestParam(value = "roleIds", required = false) Set<Integer> roleIds) {
        userService.create(user, roleIds != null ? roleIds : Set.of());
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam int id, Model model) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin?error=notfound";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.listRoles());
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute User user,
                         @RequestParam(value = "roleIds", required = false) Set<Integer> roleIds) {
        userService.update(user, roleIds != null ? roleIds : Set.of());
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}