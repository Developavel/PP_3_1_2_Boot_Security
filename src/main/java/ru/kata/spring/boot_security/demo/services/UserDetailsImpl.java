package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;

/**
 * Сервис для загрузки данных пользователя по email (логин).
 * Реализует контракт Spring Security {@link UserDetailsService}.
 * Используется при аутентификации для получения пользователя из базы данных.
 */
@Service
public class UserDetailsImpl implements UserDetailsService {

    private final UserDao userDao;

    public UserDetailsImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь c логином '" + email + "' не найден!"));
    }
}
