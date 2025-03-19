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
        Long itemId = bookingRqDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь c id=" + itemId + " не найдена"));
        if (!item.isAvailable()) {
            throw new ConditionsNotMetException("Вещь c id=" + itemId + " недоступна для бронирования");
        }
        Booking booking = bookingMapper.toEntity(bookingRqDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingRsDto(savedBooking);
    }

    @Override
    public BookingRsDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("Пользователь с id=" + ownerId + " не является владельцем вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new DataConflictException("Бронирование с id=" + bookingId + " уже обработано");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingRsDto(savedBooking);
    }

    @Override
    public BookingRsDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("У пользователя с id=" + userId + " нет доступа");
        }
        return bookingMapper.toBookingRsDto(booking);
    }

    @Override
    public List<BookingRsDto> getUserBookings(Long userId, BookingStatus status) {
        userService.findById(userId);
        return getUserBookingsByState(userId, status);
    }

    @Override
    public List<BookingRsDto> getOwnerBookings(Long ownerId, BookingStatus status) {
        userService.findById(ownerId);
        return getOwnerBookingsByState(ownerId, status);
    }

    private List<BookingRsDto> getUserBookingsByState(Long userId, BookingStatus status) {
        List<Booking> bookings;
        bookings = switch (status) {
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case APPROVED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case CANCELLED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.CANCELLED);
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };
        return bookings.stream()
                .map(bookingMapper::toBookingRsDto)
                .collect(Collectors.toList());
    }

    private List<BookingRsDto> getOwnerBookingsByState(Long ownerId, BookingStatus status) {
        List<Booking> bookings;
        bookings = switch (status) {
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case APPROVED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.APPROVED);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            case CANCELLED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.CANCELLED);
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        };
        return bookings.stream()
                .map(bookingMapper::toBookingRsDto)
                .collect(Collectors.toList());
    }
}
