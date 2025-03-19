package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingRqDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.validation.groups.Create;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID_HEADER) long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID_HEADER) long userId,
										   @RequestBody @Validated(Create.class) BookingRqDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
											 @PathVariable("bookingId") Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
												 @PathVariable("bookingId") Long bookingId,
												 @RequestParam("approved") boolean approved) {
		log.info("Approving booking {}, ownerId={}, approved={}", bookingId, ownerId, approved);
		return bookingClient.approveBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long ownerId,
												   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get owner bookings with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
		return bookingClient.getOwnerBookings(ownerId, state, from, size);
	}
}
