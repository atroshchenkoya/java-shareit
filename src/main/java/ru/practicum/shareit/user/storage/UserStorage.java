package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Optional<User> findById(Long id);

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    void delete(Long id);

    Optional<User> findByEmail(String email);
}
