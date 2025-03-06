package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingRsDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {
    public BookingRsDto toBookingRsDto(Booking booking) {
        return BookingRsDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(Item.builder()
                        .name(booking.getItem().getName())
                        .id(booking.getItem().getId())
                        .available(booking.getItem().isAvailable())
                        .build())
                .booker(User.builder()
                        .id(booking.getBooker().getId())
                        .name(booking.getBooker().getName())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public Booking toEntity(BookingRqDto bookingRqDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingRqDto.getId())
                .start(bookingRqDto.getStart())
                .end(bookingRqDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingRqDto.getStatus() != null ? bookingRqDto.getStatus() : BookingStatus.WAITING)
                .build();
    }
}
