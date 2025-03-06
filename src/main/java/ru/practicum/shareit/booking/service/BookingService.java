package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingRsDto createBooking(Long userId, BookingRqDto bookingRqDto);

    BookingRsDto approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingRsDto getBooking(Long userId, Long bookingId);

    List<BookingRsDto> getUserBookings(Long userId, BookingStatus status);

    List<BookingRsDto> getOwnerBookings(Long ownerId, BookingStatus status);
}
