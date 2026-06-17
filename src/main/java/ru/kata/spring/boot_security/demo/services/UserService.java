package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для управления пользователями.
 */
public interface UserService {

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    List<User> listUsers();

    /**
     * Находит пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return Optional с найденным пользователем
     */
    Optional<User> findById(Long id);

    /**
     * Создает нового пользователя.
     *
     * @param user    данные пользователя
     * @param roleIds идентификаторы ролей
     */
    void create(User user, Set<Long> roleIds);

    /**
     * Обновляет существующего пользователя.
     *
     * @param user         данные пользователя
     * @param roleIds      идентификаторы ролей
     * @param newPassword  новый пароль (опционально)
     */
    void update(User user, Set<Long> roleIds, String newPassword);

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     */
    void delete(Long id);
}
