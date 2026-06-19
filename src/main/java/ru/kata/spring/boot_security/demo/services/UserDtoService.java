package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.dto.UserUpdateDto;

import java.util.List;
import java.util.Optional;

public interface UserDtoService {

    List<UserResponseDto> getAllUsers();

    Optional<UserResponseDto> getUserById(Long id);

    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto updateUser(Long id, UserUpdateDto dto);

    void deleteUser(Long id);
}
