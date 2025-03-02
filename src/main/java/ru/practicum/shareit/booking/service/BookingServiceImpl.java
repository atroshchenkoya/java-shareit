package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.exception.ConditionsNotMetException;
import ru.practicum.shareit.util.exception.DataConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingRsDto createBooking(Long userId, BookingRqDto bookingRqDto) {
        User booker = userService.findById(userId);
        Item item = itemRepository.findById(bookingRqDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.isAvailable()) {
            throw new ConditionsNotMetException("Вещь недоступна для бронирования");
        }

        Booking booking = bookingMapper.toEntity(bookingRqDto, item, booker);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingRsDto(booking);
    }

    @Override
    public BookingRsDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("Пользователь не является владельцем вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new DataConflictException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingRsDto(booking);
    }

    @Override
    public BookingRsDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Нет доступа");
        }

        return bookingMapper.toBookingRsDto(booking);
    }

    @Override
    public List<BookingRsDto> getUserBookings(Long userId, String state) {
        userService.findById(userId);
        return getBookingsByState(userId, state, false);
    }

    @Override
    public List<BookingRsDto> getOwnerBookings(Long ownerId, String state) {
        userService.findById(ownerId);
        return getBookingsByState(ownerId, state, true);
    }

    private List<BookingRsDto> getBookingsByState(Long userId, String state, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case "CURRENT" -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now)
                    : bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST" -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now)
                    : bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now)
                    : bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> isOwner
                    ? bookingRepository.findByItemOwnerIdOrderByStartDesc(userId)
                    : bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingRsDto)
                .collect(Collectors.toList());
    }
}
