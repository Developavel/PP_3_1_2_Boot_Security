package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
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
    public ModelAndView showUser(@AuthenticationPrincipal User user) {
        ModelAndView modelAndView = new ModelAndView("user");
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}