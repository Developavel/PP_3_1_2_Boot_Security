package ru.kata.spring.boot_security.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private Set<RoleDto> roles;
    private String rolesAsString;
}
