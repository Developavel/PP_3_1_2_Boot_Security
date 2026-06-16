package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.services.RoleService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @GetMapping("/roles")
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }
}
