package ru.kata.spring.boot_security.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.dto.UserCreateDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.dto.UserUpdateDto;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDtoServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserDtoServiceImpl userDtoService;

    @Test
    void createUser_ShouldSetDefaultRole_WhenRoleIdsIsEmpty() {
        // Given
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("test@mail.ru");
        createDto.setPassword("pass");
        createDto.setFirstName("Test");
        createDto.setLastName("User");
        createDto.setAge(25);

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setPassword("pass");

        Role defaultRole = new Role("ROLE_USER");

        when(userService.existsByEmail("test@mail.ru")).thenReturn(false);
        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(roleService.getDefaultRole()).thenReturn(defaultRole);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userService.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(new UserResponseDto());

        // Then
        verify(userService).save(user);
        assertThat(user.getRoles()).containsExactly(defaultRole);
        assertThat(user.getPassword()).isEqualTo("encodedPass");
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Given
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("existing@mail.ru");
        createDto.setPassword("pass");

        when(userService.existsByEmail("existing@mail.ru")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userDtoService.createUser(createDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        UserUpdateDto updateDto = new UserUpdateDto();
        Set<Long> roleIds = Set.of(1L);
        updateDto.setRoleIds(roleIds);

        when(userService.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDtoService.updateUser(userId, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateUser_ShouldThrowException_WhenRoleIdsIsEmpty() {
        // Given
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setRoleIds(null);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@mail.ru");

        when(userService.findById(userId)).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userDtoService.updateUser(userId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one role");
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        doThrow(new EntityNotFoundException("User not found with ID: " + userId))
                .when(userService).deleteById(userId);

        // When & Then
        assertThatThrownBy(() -> userDtoService.deleteUser(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateUser_ShouldUpdatePassword_WhenNewPasswordProvided() {
        // Given
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("test@mail.ru");
        updateDto.setFirstName("Updated");
        updateDto.setLastName("User");
        updateDto.setAge(30);
        updateDto.setNewPassword("newPass123");
        updateDto.setRoleIds(Set.of(1L));

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@mail.ru");
        existingUser.setPassword("oldEncodedPass");

        Role role = new Role("ROLE_USER");

        when(userService.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.existsByEmail("test@mail.ru")).thenReturn(false);
        when(roleService.findById(1L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("newPass123")).thenReturn("newEncodedPass");
        when(userService.save(existingUser)).thenReturn(existingUser);

        // When
        userDtoService.updateUser(userId, updateDto);

        // Then
        verify(passwordEncoder).encode("newPass123");
        assertThat(existingUser.getPassword()).isEqualTo("newEncodedPass");
        verify(userService).save(existingUser);
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenNewPasswordIsEmpty() {
        // Given
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("test@mail.ru");
        updateDto.setFirstName("Updated");
        updateDto.setLastName("User");
        updateDto.setAge(30);
        updateDto.setNewPassword(null);
        updateDto.setRoleIds(Set.of(1L));

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@mail.ru");
        existingUser.setPassword("oldEncodedPass");

        Role role = new Role("ROLE_USER");

        when(userService.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.existsByEmail("test@mail.ru")).thenReturn(false);
        when(roleService.findById(1L)).thenReturn(Optional.of(role));
        when(userService.save(existingUser)).thenReturn(existingUser);

        // When
        userDtoService.updateUser(userId, updateDto);

        // Then
        verify(passwordEncoder, never()).encode(anyString());
        assertThat(existingUser.getPassword()).isEqualTo("oldEncodedPass");
        verify(userService).save(existingUser);
    }
}
