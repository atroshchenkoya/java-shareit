package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.util.validation.groups.Create;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
	private BookingState status;
}
