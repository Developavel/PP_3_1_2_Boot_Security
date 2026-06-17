package ru.kata.spring.boot_security.demo.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для AdminRestController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Проверяет, что администратор получает список пользователей (200 OK).
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnOk_ForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    /**
     * Проверяет, что обычный пользователь получает ошибку доступа (403 Forbidden).
     */
    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldBeForbidden_ForUser() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}
