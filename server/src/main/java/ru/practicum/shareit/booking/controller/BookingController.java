package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

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
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @RequestBody BookingDtoIn bookingDtoIn) {
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, bookingDtoIn));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                  @PathVariable Long bookingId,
                                  @RequestParam("approved") Boolean approved) {
        log.info("Получен Patch запрос к эндпоинту /bookings/{bookingId}?approved={approved}");
        return BookingMapper.toBookingDto(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long bookingId) {
        log.info("Получен Get запрос к эндпоинту /bookings/{bookingId}");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<Booking> getBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @RequestParam(value = "state", defaultValue = "ALL") String stateText,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "20") Integer size) {
        State state = BookingStatus.findState(stateText);
        log.info("Получен Get запрос к эндпоинту /bookings?state={}from={}size={}", stateText, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return bookingService.getBookingsByUser(userId, state, pageRequest);
    }

    @GetMapping("/owner")
    public Collection<Booking> getBookingsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                          @RequestParam(value = "state", defaultValue = "ALL") String stateText,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "20") Integer size) {
        State state = BookingStatus.findState(stateText);
        log.info("Получен Get запрос к эндпоинту /bookings/owner?state={}from={}size={}", stateText, from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return bookingService.getBookingsByOwner(ownerId, state, pageRequest);
    }

    public PageRequest findPageRequest(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
