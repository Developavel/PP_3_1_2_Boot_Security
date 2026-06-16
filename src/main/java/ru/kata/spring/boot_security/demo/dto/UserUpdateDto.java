package ru.kata.spring.boot_security.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;
/**
 * DTO для обновления пользователя.
 * Используется в AdminRestController для обновления пользователей.
 */
@Data
@NoArgsConstructor
public class UserUpdateDto {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 150, message = "Age cannot exceed 150 years")
    private int age;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 4, message = "Password must be at least 4 characters")
    private String newPassword;  // Опционально, только если нужно сменить пароль

    private Set<Long> roleIds;
}