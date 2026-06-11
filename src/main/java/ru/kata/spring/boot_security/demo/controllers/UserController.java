package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;

/**
 * Контроллер для работы с профилем текущего пользователя.
 * Обрабатывает запросы к странице /user, отображая информацию о пользователе,
 * полученную через {@link AuthenticationPrincipal}.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public String showUser(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "user";
    }
}