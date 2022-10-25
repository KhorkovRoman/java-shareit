package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
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
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                //.itemId(booking.getItem().getId())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                //.bookerId(booking.getBooker().getId())
                .build();
    }

    public static BookingDtoItem toBookingDtoItem(Booking booking) {
        return BookingDtoItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                //.itemId(booking.getItem().getId())
                .status(booking.getStatus())
                //.booker(booking.getBooker())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(Long bookingId, User booker, Item item, BookingDtoIn bookingDtoIn) {
        return Booking.builder()
                .id(bookingId)
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDtoIn.getStatus())
                .build();
    }
}
