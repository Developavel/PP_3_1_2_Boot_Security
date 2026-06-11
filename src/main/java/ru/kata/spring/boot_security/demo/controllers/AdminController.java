//package ru.kata.spring.boot_security.demo.controllers;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import ru.kata.spring.boot_security.demo.model.User;
//import ru.kata.spring.boot_security.demo.services.RoleService;
//import ru.kata.spring.boot_security.demo.services.UserService;
//
//import java.util.Set;
//
///**
// * Контроллер панели администратора.
// * Обрабатывает запросы на отображение списка пользователей, создание,
// * редактирование, удаление пользователей и назначение ролей.
// */
//@Controller
//@RequestMapping("/admin")
//@RequiredArgsConstructor
//public class AdminController {
//
//    private final UserService userService;
//    private final RoleService roleService;
//
//    @GetMapping
//    public String showUsers(Model model) {
//        model.addAttribute("users", userService.listUsers());
//        model.addAttribute("allRoles", roleService.getAllRoles());
//        return "admin/index";
//    }
//
//    @GetMapping("/new")
//    public String showCreateForm(Model model) {
//        model.addAttribute("user", new User());
//        model.addAttribute("allRoles", roleService.getAllRoles());
//        return "admin/new";
//    }
//
//    @PostMapping
//    public String create(@ModelAttribute User user,
//                         @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
//        userService.create(user, roleIds != null ? roleIds : Set.of());
//        return "redirect:/admin";
//    }
//
//    @PostMapping("/edit")
//    public String update(@ModelAttribute User user,
//                         @RequestParam(value = "roleIds", required = false) Set<Long> roleIds,
//                         @RequestParam(value = "newPassword", required = false) String newPassword) {
//        userService.update(user, roleIds != null ? roleIds : Set.of(), newPassword);
//        return "redirect:/admin";
//    }
//
//    @PostMapping("/delete")
//    public String delete(@RequestParam Long id) {
//        userService.delete(id);
//        return "redirect:/admin";
//    }
//}
package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Collections;
import java.util.Set;

/**
 * Контроллер панели администратора.
 * Обрабатывает CRUD-операции для пользователей: просмотр, создание, редактирование и удаление
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final String REDIRECT_ADMIN = "redirect:/admin";
    private static final String ADMIN_INDEX = "admin/index";
    private static final String ADMIN_NEW = "admin/new";

    private final UserService userService;
    private final RoleService roleService;

    /**
     * Отображает список всех пользователей с их ролями.
     */
    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", userService.listUsers());
        addRolesToModel(model);
        return ADMIN_INDEX;
    }

    /**
     * Отображает форму для создания нового пользователя.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        addRolesToModel(model);
        return ADMIN_NEW;
    }

    /**
     * Создаёт нового пользователя.
     * @param user данные из формы
     * @param roleIds идентификаторы выбранных ролей
     */
    @PostMapping
    public String create(@ModelAttribute User user,
                         @RequestParam(required = false) Set<Long> roleIds) {
        userService.create(user, safeRoles(roleIds));
        return REDIRECT_ADMIN;
    }

    /**
     * Обновляет данные существующего пользователя.
     * @param user обновлённые данные
     * @param roleIds идентификаторы выбранных ролей
     * @param newPassword новый пароль (если указан)
     */
    @PostMapping("/edit")
    public String update(@ModelAttribute User user,
                         @RequestParam(required = false) Set<Long> roleIds,
                         @RequestParam(required = false) String newPassword) {
        userService.update(user, safeRoles(roleIds), newPassword);
        return REDIRECT_ADMIN;
    }

    /**
     * Удаляет пользователя по идентификатору.
     */
    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        userService.delete(id);
        return REDIRECT_ADMIN;
    }

    /**
     * Добавляет в модель список всех доступных ролей (для отображения в формах).
     */
    private void addRolesToModel(Model model) {
        model.addAttribute("allRoles", roleService.getAllRoles());
    }

    /**
     * Преобразует nullable Set ролей в безопасный (не null, не пустой).
     * Если roleIds == null, возвращает пустой Set.
     */
    private Set<Long> safeRoles(Set<Long> roleIds) {
        return roleIds == null ? Collections.emptySet() : roleIds;
    }
}