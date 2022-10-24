package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@SuppressWarnings("ALL")
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	//createBooking
	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID_HEADER) long userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
								                @PathVariable Long bookingId,
								                @RequestParam boolean approved) {
		log.info("Получен Patch запрос к эндпоинту /bookings/{bookingId}?approved={approved}");
		return bookingClient.approveBooking(ownerId, bookingId, approved);
	}

    //getBookingById
	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

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
}
