package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;

import java.util.List;

public interface BookingService {
    BookingRsDto createBooking(Long userId, BookingRqDto bookingRqDto);
    BookingRsDto approveBooking(Long ownerId, Long bookingId, boolean approved);
    BookingRsDto getBooking(Long userId, Long bookingId);
    List<BookingRsDto> getUserBookings(Long userId, String state);
    List<BookingRsDto> getOwnerBookings(Long ownerId, String state);
}
