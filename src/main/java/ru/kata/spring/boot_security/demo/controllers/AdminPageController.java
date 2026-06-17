package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для отображения страницы администратора.
 */
@Controller
@RequestMapping("/admin")
public class AdminPageController {

    /**
     * Возвращает страницу администратора.
     *
     * @return имя шаблона admin.html
     */
    @GetMapping
    public String adminPage() {
        return "admin";
    }
}
