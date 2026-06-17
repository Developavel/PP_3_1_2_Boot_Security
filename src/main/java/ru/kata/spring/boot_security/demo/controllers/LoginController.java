package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для отображения страницы входа.
 */
@Controller
public class LoginController {

    /**
     * Возвращает страницу входа.
     *
     * @return имя шаблона login.html
     */
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }
}
