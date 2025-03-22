package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.util.exception.DataConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldSaveUser() {
        User user = new User(null, "John Doe", "john@example.com");
        User savedUser = userService.create(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void createUser_shouldSetNameFromEmail_whenNameIsNull() {
        User user = new User(null, null, "no-name@example.com");
        User savedUser = userService.create(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("no-name@example.com");
        assertThat(savedUser.getEmail()).isEqualTo("no-name@example.com");
    }

    @Test
    void findById_shouldReturnUser() {
        User user = userRepository.save(new User(null, "Jane Doe", "jane@example.com"));
        User foundUser = userService.findById(user.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void findById_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=999 не найден.");
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userRepository.save(new User(null, "User1", "user1@example.com"));
        userRepository.save(new User(null, "User2", "user2@example.com"));

        List<User> users = (List<User>) userService.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void partialUpdate_shouldUpdateNameOnly() {
        User user = userRepository.save(new User(null, "Old Name", "email@example.com"));

        User updates = new User(user.getId(), "New Name", null);
        User updatedUser = userService.partialUpdate(updates);

        assertThat(updatedUser.getName()).isEqualTo("New Name");
        assertThat(updatedUser.getEmail()).isEqualTo("email@example.com");
    }

    @Test
    void partialUpdate_shouldUpdateEmailOnly() {
        User user = userRepository.save(new User(null, "User", "old-email@example.com"));

        User updates = new User(user.getId(), null, "new-email@example.com");
        User updatedUser = userService.partialUpdate(updates);

        assertThat(updatedUser.getName()).isEqualTo("User");
        assertThat(updatedUser.getEmail()).isEqualTo("new-email@example.com");
    }

    @Test
    void partialUpdate_shouldThrowConflictOnDuplicateEmail() {
        userRepository.save(new User(null, "User1", "unique@example.com"));
        User user2 = userRepository.save(new User(null, "User2", "test@example.com"));

        User updates = new User(user2.getId(), "Updated Name", "unique@example.com");

        assertThatThrownBy(() -> userService.partialUpdate(updates))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Пользователь с email=unique@example.com уже существует.");
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = userRepository.save(new User(null, "ToDelete", "delete@example.com"));

        userService.delete(user.getId());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void delete_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=999 не найден.");
    }

    @Test
    void checkUser_shouldNotThrowException_whenUserExists() {
        User user = userRepository.save(new User(null, "User", "user@example.com"));

        assertThatCode(() -> userService.checkUser(user.getId())).doesNotThrowAnyException();
    }

    @Test
    void checkUser_shouldThrowNotFoundException_whenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.checkUser(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=999 не найден.");
    }
}
