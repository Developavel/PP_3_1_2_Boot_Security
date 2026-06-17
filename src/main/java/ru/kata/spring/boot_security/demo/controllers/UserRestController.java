package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;

/**
 * REST-контроллер для получения данных текущего пользователя.
 */
@RestController
@RequestMapping("/api/user")
public class UserRestController {

    /**
     * Возвращает текущего авторизованного пользователя.
     *
     * @param user текущий пользователь (извлекается из SecurityContext)
     * @return текущий пользователь
     */
    @GetMapping
    public User getCurrentUser(@AuthenticationPrincipal User user) {
        return user;
    }
}
