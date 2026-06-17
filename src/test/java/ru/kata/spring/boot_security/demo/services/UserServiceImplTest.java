package ru.kata.spring.boot_security.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Проверяет, что при создании пользователя без ролей назначается роль по умолчанию.
     */
    @Test
    void create_ShouldSetDefaultRole_WhenRoleIdsIsEmpty() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setPassword("pass");
        Role defaultRole = new Role("ROLE_USER");

        when(roleService.getDefaultRole()).thenReturn(defaultRole);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        userService.create(user, null);

        verify(userRepository).save(user);
        assertThat(user.getRoles()).containsExactly(defaultRole);
        assertThat(user.getPassword()).isEqualTo("encodedPass");
    }

    /**
     * Проверяет, что при создании пользователя с существующим email выбрасывается исключение.
     */
    @Test
    void create_ShouldThrowException_WhenEmailExists() {
        User user = new User();
        user.setEmail("existing@mail.ru");
        user.setPassword("pass");

        when(userRepository.existsByEmail("existing@mail.ru")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(user, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("уже существует");
    }

    /**
     * Проверяет, что при обновлении без ролей выбрасывается исключение.
     */
    @Test
    void update_ShouldThrowException_WhenRoleIdsIsEmpty() {
        User user = new User();
        user.setId(1L);

        assertThatThrownBy(() -> userService.update(user, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("одну роль");
    }

    /**
     * Проверяет, что при обновлении несуществующего пользователя выбрасывается исключение.
     */
    @Test
    void update_ShouldThrowException_WhenUserNotFound() {
        User user = new User();
        user.setId(999L);
        Set<Long> roleIds = Set.of(1L);

        when(userRepository.findByIdWithRoles(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(user, roleIds, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    /**
     * Проверяет, что при удалении несуществующего пользователя выбрасывается исключение.
     */
    @Test
    void delete_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }
}
