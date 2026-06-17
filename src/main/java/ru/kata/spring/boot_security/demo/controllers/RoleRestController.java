package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.services.RoleService;

import java.util.List;

/**
 * REST-контроллер для получения списка ролей.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    /**
     * Возвращает список всех доступных ролей.
     *
     * @return список ролей
     */
    @GetMapping("/roles")
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }
}
