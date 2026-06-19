package ru.kata.spring.boot_security.demo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.dto.UserUpdateDto;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDtoServiceImpl implements UserDtoService {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userService.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponseDto> getUserById(Long id) {
        return userService.findById(id)
                .map(userMapper::toResponseDto);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        log.info("Creating user: {}", dto.getEmail());

        if (userService.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "User with email '" + dto.getEmail() + "' already exists!");
        }

        User user = userMapper.toEntity(dto);

        Set<Long> roleIds = dto.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            user.setRoles(Set.of(roleService.getDefaultRole()));
        } else {
            user.setRoles(convertIdsToRoles(roleIds));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        log.info("User created successfully: {}", user.getEmail());
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        log.info("Updating user with ID: {}", id);

        User existing = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        if (!existing.getEmail().equals(dto.getEmail()) &&
                userService.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "User with email '" + dto.getEmail() + "' already exists!");
        }

        userMapper.merge(existing, dto);

        Set<Long> roleIds = dto.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role!");
        }
        existing.setRoles(convertIdsToRoles(roleIds));

        String newPassword = dto.getNewPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            existing.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.save(existing);

        log.info("User updated successfully: {}", existing.getEmail());
        return userMapper.toResponseDto(existing);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteById(id);
        log.info("User deleted successfully: ID {}", id);
    }

    private Set<Role> convertIdsToRoles(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return roleIds.stream()
                .map(id -> roleService.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + id)))
                .collect(Collectors.toSet());
    }
}
