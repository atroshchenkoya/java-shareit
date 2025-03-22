package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.exception.ConditionsNotMetException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnauthorizedException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingRepository bookingRepository;


    @Test
    void addItem_shouldSaveItem() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build();

        ItemDto savedItem = itemService.addItem(owner.getId(), itemDto);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Item Name");
        assertThat(savedItem.getDescription()).isEqualTo("Item Description");
    }

    @Test
    void addItem_shouldThrowNotFoundException_whenUserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        assertThatThrownBy(() -> itemService.addItem(999L, itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=999 не найден.");
    }

    @Test
    void updateItem_shouldUpdateItem() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(owner)
                .build());

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name("New Name")
                .description("New Description")
                .available(false)
                .owner(owner)
                .build();

        ItemDto updatedItem = itemService.updateItem(owner.getId(), itemDto);

        assertThat(updatedItem.getName()).isEqualTo("New Name");
        assertThat(updatedItem.getDescription()).isEqualTo("New Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemNotFound() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .id(999L)
                .name("New Name")
                .description("New Description")
                .available(true)
                .owner(owner)
                .build();

        assertThatThrownBy(() -> itemService.updateItem(owner.getId(), itemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id=999 не найдена.");
    }

    @Test
    void updateItem_shouldThrowUnauthorizedException_whenUserIsNotOwner() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User otherUser = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(owner)
                .build());


        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name("New Name")
                .description("New Description")
                .available(false)
                .build();


        assertThatThrownBy(() -> itemService.updateItem(otherUser.getId(), itemDto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Пользователь с id=" + otherUser.getId() + " не является владельцем вещи.");
    }

    @Test
    void getItemById_shouldReturnItem() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        ItemDto foundItem = itemService.getItemById(item.getId());

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(item.getId());
        assertThat(foundItem.getName()).isEqualTo("Item Name");
    }

    @Test
    void getItemById_shouldThrowNotFoundException_whenItemNotFound() {
        NotFoundException exception = catchThrowableOfType(() -> itemService.getItemById(999L), NotFoundException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Вещь с id=999 не найдена");
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenNoMatch() {
        var items = itemService.searchItems("Non Existent");

        assertThat(items).isEmpty();
    }

    @Test
    void getUserItems_shouldReturnUserItems() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        itemRepository.save(Item.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .owner(owner)
                .build());

        var items = itemService.getUserItems(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting("name").containsExactlyInAnyOrder("Drill", "Hammer");
    }

    @Test
    void getUserItems_shouldReturnEmptyList_whenUserHasNoItems() {
        User user = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        var items = itemService.getUserItems(user.getId());

        assertThat(items).isEmpty();
    }

    @Test
    void addComment_shouldSaveComment_whenUserHadBooking() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User renter = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(renter)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1)) // аренда завершена
                .status(BookingStatus.APPROVED)
                .build());

        CommentDto commentDto = CommentDto.builder()
                .text("Great tool!")
                .build();

        CommentDto savedComment = itemService.addComment(item.getId(), renter.getId(), commentDto);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Great tool!");
        assertThat(savedComment.getAuthorName()).isEqualTo("Jane Doe");
    }

    @Test
    void addComment_shouldThrowException_whenBookingNotFinished() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User renter = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(renter)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.APPROVED)
                .build());

        CommentDto commentDto = CommentDto.builder()
                .text("Great tool!")
                .build();

        assertThatThrownBy(() -> itemService.addComment(item.getId(), renter.getId(), commentDto))
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Пользователь c id=" + renter.getId() + " не брал вещь c id=" + item.getId() + " в аренду или аренда еще не завершена");
    }

}