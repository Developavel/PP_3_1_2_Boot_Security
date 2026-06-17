package ru.kata.spring.boot_security.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Set;

/**
 * DTO для ответа с данными пользователя.
 * Исключает чувствительные поля (например, пароль).
 */
@Data
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private Set<Role> roles;
    private String rolesAsString;

    /**
     * Конвертирует User в UserResponseDto
     */
    public static UserResponseDto fromUser(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAge(user.getAge());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setRolesAsString(user.getRolesAsString());
        return dto;
    }
}