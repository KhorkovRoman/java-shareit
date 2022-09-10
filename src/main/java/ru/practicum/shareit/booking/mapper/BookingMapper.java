package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingMapper {
    public static Collection<BookingDto> toBookingDtoCollection(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getStatus(),
                booking.getBooker().getId()
        );
    }

    public static Booking toBooking(Long bookingId, User booker, Item item, BookingDtoIn bookingDtoIn) {
        return new Booking(
                bookingId,
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd(),
                item,
                booker,
                bookingDtoIn.getStatus()
        );
    }
}
