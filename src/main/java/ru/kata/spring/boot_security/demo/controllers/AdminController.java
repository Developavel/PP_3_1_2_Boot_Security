package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String newUser(Model model) {
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
    public String edit(Model model, @RequestParam("id") int id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.listRoles());
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String update(@RequestParam("id") int id,
                         @RequestParam("username") String username,
                         @RequestParam("lastname") String lastname,
                         @RequestParam(value = "newPassword", required = false) String newPassword,
                         @RequestParam(value = "roleIds", required = false) Set<Integer> roleIds) {
        userService.update(id, username, lastname, newPassword, roleIds);
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
