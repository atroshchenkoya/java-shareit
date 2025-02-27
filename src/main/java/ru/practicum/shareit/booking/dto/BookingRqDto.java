package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.util.validation.ValidBookingDates;
import ru.practicum.shareit.util.validation.groups.Create;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidBookingDates(groups = Create.class)
public class BookingRqDto {
    private Long id;

    @NotNull(message = "Дата начала не может быть пустой.", groups = Create.class)
    @FutureOrPresent(message = "Дата начала не может быть в прошлом.", groups = Create.class)
    private LocalDateTime start;

    @NotNull(message = "Дата окончания не может быть пустой.", groups = Create.class)
    @Future(message = "Дата окончания должна быть в будущем.", groups = Create.class)
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private BookingStatus status;
}
