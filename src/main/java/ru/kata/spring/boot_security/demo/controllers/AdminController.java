package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.UserService;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleDao roleDao;   // ← теперь напрямую используем DAO

    public AdminController(UserService userService, RoleDao roleDao) {
        this.userService = userService;
        this.roleDao = roleDao;
    }

    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userService.listUsers());
        return "admin/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleDao.listRoles());   // ← вызов DAO
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
        model.addAttribute("allRoles", roleDao.listRoles());   // ← вызов DAO
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute User user,
                         @RequestParam(value = "roleIds", required = false) Set<Integer> roleIds,
                         @RequestParam(value = "newPassword", required = false) String newPassword) {
        userService.update(user, roleIds != null ? roleIds : Set.of(), newPassword);
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
