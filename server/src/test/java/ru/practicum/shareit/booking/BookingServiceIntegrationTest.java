package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.exception.ConditionsNotMetException;
import ru.practicum.shareit.util.exception.DataConflictException;
import ru.practicum.shareit.util.exception.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createBooking_shouldSaveBooking_whenItemIsAvailable() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        BookingRqDto bookingRqDto = BookingRqDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingRsDto savedBooking = bookingService.createBooking(booker.getId(), bookingRqDto);

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void createBooking_shouldThrowException_whenItemIsUnavailable() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(false)  // Недоступна
                .owner(owner)
                .build());

        BookingRqDto bookingRqDto = BookingRqDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), bookingRqDto))
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Вещь c id=" + item.getId() + " недоступна для бронирования");
    }

    @Test
    void approveBooking_shouldApproveBooking_whenOwnerConfirms() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        BookingRsDto approvedBooking = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_shouldThrowException_whenBookingAlreadyProcessed() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build());

        assertThatThrownBy(() -> bookingService.approveBooking(owner.getId(), booking.getId(), true))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Бронирование с id=" + booking.getId() + " уже обработано");
    }

    @Test
    void getBooking_shouldReturnBooking_whenUserIsOwnerOrBooker() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        BookingRsDto foundBookingByBooker = bookingService.getBooking(booker.getId(), booking.getId());
        BookingRsDto foundBookingByOwner = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(foundBookingByBooker.getId()).isEqualTo(booking.getId());
        assertThat(foundBookingByOwner.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBooking_shouldThrowException_whenUserHasNoAccess() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        User stranger = userRepository.save(User.builder()
                .name("Stranger")
                .email("stranger@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        assertThatThrownBy(() -> bookingService.getBooking(stranger.getId(), booking.getId()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("У пользователя с id=" + stranger.getId() + " нет доступа");
    }

    @Test
    void getUserBookings_shouldReturnBookings_whenUserExists() {
        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(booker)
                .build());

        Booking booking1 = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        Booking booking2 = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        List<BookingRsDto> bookings = bookingService.getUserBookings(booker.getId(), BookingStatus.ALL);

        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getUserBookings_shouldReturnEmptyList_whenNoBookings() {
        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        List<BookingRsDto> bookings = bookingService.getUserBookings(booker.getId(), BookingStatus.ALL);

        assertThat(bookings.size()).isEqualTo(0);
    }

    @Test
    void getOwnerBookings_shouldReturnBookings_whenOwnerHasBookings() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        List<BookingRsDto> ownerBookings = bookingService.getOwnerBookings(owner.getId(), BookingStatus.ALL);

        assertThat(ownerBookings.size()).isEqualTo(1);
        assertThat(ownerBookings.getFirst().getId()).isEqualTo(booking.getId());
    }

    @Test
    void getOwnerBookings_shouldReturnEmptyList_whenNoBookings() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        List<BookingRsDto> bookings = bookingService.getOwnerBookings(owner.getId(), BookingStatus.ALL);

        assertThat(bookings.size()).isEqualTo(0);
    }

    @Test
    void getUserBookings_shouldFilterByStatus() {
        User booker = userRepository.save(User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(booker)
                .build());

        Booking waitingBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build());

        List<BookingRsDto> waitingBookings = bookingService.getUserBookings(booker.getId(), BookingStatus.WAITING);

        assertThat(waitingBookings.size()).isEqualTo(1);
        assertThat(waitingBookings.getFirst().getId()).isEqualTo(waitingBooking.getId());
    }

    @Test
    void getOwnerBookings_shouldFilterByStatus() {
        User owner = userRepository.save(User.builder()
                .name("John Doe")
                .email("john@example.com")
                .build());

        User booker = userRepository.save(User.builder()
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
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build());

        Booking rejectedBooking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.REJECTED)
                .build());

        List<BookingRsDto> rejectedBookings = bookingService.getOwnerBookings(owner.getId(), BookingStatus.REJECTED);

        assertThat(rejectedBookings.size()).isEqualTo(1);
        assertThat(rejectedBookings.getFirst().getId()).isEqualTo(rejectedBooking.getId());
    }

}
