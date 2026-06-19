package ru.kata.spring.boot_security.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@mail.ru");

        when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
        verify(userRepository).findByIdWithRoles(userId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByIdWithRoles(userId);
    }

    @Test
    void save_ShouldCallRepositorySave() {
        // Given
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setPassword("pass");

        when(userRepository.save(user)).thenReturn(user);

        // When
        User result = userService.save(user);

        // Then
        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteById_ShouldDelete_WhenExists() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteById(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteById_ShouldThrowException_WhenNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        // Given
        String email = "test@mail.ru";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenNotFound() {
        // Given
        String email = "nonexistent@mail.ru";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        // Given
        when(userRepository.findAllWithRoles()).thenReturn(java.util.List.of(new User(), new User()));

        // When
        java.util.List<User> result = userService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(userRepository).findAllWithRoles();
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // Given
        String email = "test@mail.ru";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        verify(userRepository).findByEmailWithRoles(email);
    }
}
