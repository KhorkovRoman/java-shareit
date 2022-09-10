package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.exeption.UnknownStateException;

import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("checkstyle:Regexp")
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                       @Validated({Create.class}) @RequestBody BookingDtoIn bookingDtoIn) {
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, bookingDtoIn));
    }

    @PatchMapping("/{bookingId}")
    Booking approveBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        log.info("Получен Patch запрос к эндпоинту /bookings/{bookingId}?approved={approved}");
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    Booking getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                           @PathVariable Long bookingId) {
        log.info("Получен Get запрос к эндпоинту /bookings/{bookingId}");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    Collection<Booking> getBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @RequestParam(value = "state", defaultValue = "ALL") String stateText) {
        State state = setState(stateText);
        log.info("Получен Get запрос к эндпоинту /bookings?state={state}");
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    Collection<Booking> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                          @RequestParam(value = "state", defaultValue = "ALL") String stateText) {
        State state = setState(stateText);
        log.info("Получен Get запрос к эндпоинту /bookings/owner?state={state}");
        return bookingService.getBookingsByOwner(ownerId, state);
    }

    public static State setState(String stateText) {
        boolean checkState = false;
        for (State state: State.values()) {
            if (Objects.equals(state.toString(), stateText)) {
                checkState = true;
                break;
            }
        }
        if (!checkState) {
            throw new UnknownStateException("Unknown state: " + stateText);
        }
        return State.valueOf(stateText);
    }
}
