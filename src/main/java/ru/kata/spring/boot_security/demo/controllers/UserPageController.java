package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для отображения страницы пользователя.
 */
@Controller
@RequestMapping("/user")
public class UserPageController {

    /**
     * Возвращает страницу пользователя.
     *
     * @return имя шаблона user.html
     */
    @GetMapping
    public String userPage() {
        return "user";
    }
}
