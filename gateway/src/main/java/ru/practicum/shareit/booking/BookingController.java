package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.common.ValidationPageParam;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

	private ValidationPageParam validationPageParam;

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
										        @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.createBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) @Min(1) @Max(1000000) Long ownerId,
								                 @PathVariable  Long bookingId,
								                 @RequestParam("approved") Boolean approved) {
		log.info("Received request to endpoint PATCH/bookings/{}?approved={} from ownerId={}",
				                                               bookingId, approved, ownerId);
		return bookingClient.approveBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) @Min(1) @Max(1000000) Long userId,
											 @PathVariable  Long bookingId) {
		log.info("Received request to endpoint GET/bookings/{} from userId={}", bookingId, userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
							  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
							  	    @Positive @RequestParam(defaultValue = "20") Integer size) {
		validatePage(from, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
											@RequestParam(name = "state", defaultValue = "all") String stateParam,
							@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
				     			  @Positive @RequestParam(defaultValue = "20") Integer size) {
		validatePage(from, size);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
		return bookingClient.getBookingsByOwner(ownerId, state, from, size);
	}

	private void validatePage(Integer from, Integer size) {
		validationPageParam = new ValidationPageParam(from, size);
		validationPageParam.validatePageParam();
	}
}
