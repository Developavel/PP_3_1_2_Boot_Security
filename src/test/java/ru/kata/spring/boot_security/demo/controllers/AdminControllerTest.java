package ru.kata.spring.boot_security.demo.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnOk_ForAdmin() throws Exception {
        mockMvc.perform(get("/admin/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldBeForbidden_ForUser() throws Exception {
        mockMvc.perform(get("/admin/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_ShouldBeUnauthorized_ForAnonymous() throws Exception {
        mockMvc.perform(get("/admin/api/users"))
                .andExpect(status().is3xxRedirection()); // Редирект на страницу логина
    }
}
