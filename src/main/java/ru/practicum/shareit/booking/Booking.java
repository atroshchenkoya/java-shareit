package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Data
@Builder(toBuilder = true)
public class Booking {
    private Long id;
    private Long start;
    private Long end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
