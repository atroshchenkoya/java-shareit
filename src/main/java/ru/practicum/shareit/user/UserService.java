package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.util.exception.DataConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        checkEmailConflict(user);
        setNameByLoginIfNameIsNull(user);
        return userRepository.save(user);
    }

    public User partialUpdate(User updates) {
        User existingUser = findById(updates.getId());

        if (updates.getEmail() != null && !updates.getEmail().isBlank()) {
            checkEmailConflict(updates);
            existingUser.setEmail(updates.getEmail());
        }

        if (updates.getName() != null && !updates.getName().isBlank()) {
            existingUser.setName(updates.getName());
        }

        return userRepository.save(existingUser);
    }

    public void delete(long id) {
        checkUser(id);
        userRepository.deleteById(id);
    }

    private void setNameByLoginIfNameIsNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getEmail());
            log.info("Имя пользователя не указано. Используется email: {}", user.getName());
        }
    }

    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    private void checkEmailConflict(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    if (!Objects.equals(existingUser.getId(), user.getId())) {
                        throw new DataConflictException("Пользователь с email=" + user.getEmail() + " уже существует.");
                    }
                });
    }
}
