package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.validation.groups.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingRsDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestBody @Validated(Create.class) BookingRqDto bookingRqDto) {
        return bookingService.createBooking(userId, bookingRqDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingRsDto approveBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                       @PathVariable("bookingId") Long bookingId,
                                       @RequestParam("approved") boolean approved) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingRsDto getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingRsDto> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") BookingStatus state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingRsDto> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                               @RequestParam(required = false) BookingStatus status) {
        return bookingService.getOwnerBookings(ownerId, status);
    }
}