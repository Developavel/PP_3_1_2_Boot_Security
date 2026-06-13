package ru.kata.spring.boot_security.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_ShouldSetDefaultRole_WhenRoleIdsIsEmpty() {
        User user = new User();
        user.setPassword("pass");
        Role defaultRole = new Role("ROLE_USER");
        when(roleService.getDefaultRole()).thenReturn(defaultRole);

        userService.create(user, null);

        verify(userDao).save(user);
        assertThat(user.getRoles()).containsExactly(defaultRole);
    }

    @Test
    void update_ShouldThrowException_WhenRoleIdsIsEmpty() {
        User user = new User();
        user.setId(1L);
        assertThatThrownBy(() -> userService.update(user, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("одну роль");
    }
}
