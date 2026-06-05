package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService,
                           RoleService roleService,
                           RoleDao roleDao,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
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
                         @RequestParam(value = "roleIds", required = false) String roleIds) {

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String idStr : roleIds.split(",")) {
                try {
                    int roleId = Integer.parseInt(idStr.trim());
                    Role role = roleDao.findById(roleId);
                    if (role != null) {
                        roles.add(role);
                    }
                } catch (NumberFormatException e) {
                    // игнорируем
                }
            }
            user.setRoles(roles);
        }

        userService.create(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String edit(Model model, @RequestParam("id") int id) {
        model.addAttribute("user", userService.show(id));
        model.addAttribute("allRoles", roleService.listRoles());
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute User user,
                         @RequestParam(value = "newPassword", required = false) String newPassword,
                         @RequestParam(value = "roleIds", required = false) String roleIds) {

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            User existingUser = userService.findById(user.getId());
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String idStr : roleIds.split(",")) {
                int roleId = Integer.parseInt(idStr.trim());
                Role role = roleDao.findById(roleId);
                if (role != null) {
                    roles.add(role);
                }
            }
            user.setRoles(roles);
        }

        userService.update(user);
        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
