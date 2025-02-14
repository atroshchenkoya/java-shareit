package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.exception.DataConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        checkEmailConflict(user);
        setNameByLoginIfNameIsNull(user);
        return userStorage.create(user);
    }

    public User partialUpdate(Long id, User updates) {
        User existingUser = findById(id);

        if (updates.getEmail() != null && !updates.getEmail().isBlank()) {
            checkEmailConflict(updates);
            existingUser.setEmail(updates.getEmail());
        }

        if (updates.getName() != null && !updates.getName().isBlank()) {
            existingUser.setName(updates.getName());
        }

        return userStorage.update(existingUser);
    }

    public void delete(long id) {
        checkUser(id);
        userStorage.delete(id);
    }

    private void setNameByLoginIfNameIsNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getEmail());
            log.info("Имя пользователя не указано. Используется email: {}", user.getName());
        }
    }

    public void checkUser(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    private void checkEmailConflict(User user) {
        User userFromStorage = userStorage.findByEmail(user.getEmail()).orElse(null);
        if (Objects.nonNull(userFromStorage)) {
            throw new DataConflictException("Пользователь с email=" + user.getEmail() + " уже существует.");
        }
    }
}
