package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingMapperTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private BookingMapper bookingMapper;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private Collection<Item> itemCollection = new ArrayList<>();

    private ItemRequest itemRequest;

    //bookings by user2
    private Booking bookingByUser2;
    private LocalDateTime timeStartBooking2;
    private LocalDateTime timeEndBooking2;

    private Booking bookingOwnerWaiting;
    private LocalDateTime timeStartBookingOwnerWaiting;
    private LocalDateTime timeEndBookingOwnerWaiting;

    private Booking bookingOwnerRejected;
    private LocalDateTime timeStartBookingOwnerRej;
    private LocalDateTime timeEndBookingOwnerRej;

    private BookingDtoIn bookingDtoIn;

    private Collection<Booking> bookingCollection = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        bookingMapper = new BookingMapper();

        user1 = userRepository.save(new User(1L, "User 1 name", "user1@email.ru"));
        itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Need Item 2 name", user1, LocalDateTime.now()));
        item1 = itemRepository.save(
                new Item(1L, "item 1", "item 1 desciption", true, user1, null));

        user2 = userRepository.save(new User(2L, "User 2 name", "user2@email.ru"));

        item2 = itemRepository.save(
                new Item(2L, "item 2", "item 2 desciption", true, user2, itemRequest));

        itemCollection.add(item1);
        itemCollection.add(item2);

        timeStartBooking2 = LocalDateTime.of(2023, 10, 10, 12, 0);
        timeEndBooking2 = LocalDateTime.of(2023, 10, 11, 12, 0);
        bookingByUser2 = bookingRepository.save(new Booking(1L, timeStartBooking2, timeEndBooking2,
                item1, user2, BookingStatus.APPROVED));

        //bookingByOwnerWaiting
        timeStartBookingOwnerWaiting = LocalDateTime.of(2023, 10, 20, 12, 0);
        timeEndBookingOwnerWaiting = LocalDateTime.of(2023, 10, 21, 12, 0);
        bookingOwnerWaiting = bookingRepository.save(new Booking(2L,
                timeStartBookingOwnerWaiting, timeEndBookingOwnerWaiting,
                item1, user2, BookingStatus.WAITING));

        //bookingByOwnerRejected
        timeStartBookingOwnerRej = LocalDateTime.of(2023, 10, 30, 12, 0);
        timeEndBookingOwnerRej = LocalDateTime.of(2023, 10, 31, 12, 0);
        bookingOwnerRejected = bookingRepository.save(new Booking(3L,
                timeStartBookingOwnerRej, timeEndBookingOwnerRej,
                item1, user2, BookingStatus.REJECTED));

        bookingDtoIn = new BookingDtoIn(1L, LocalDateTime.of(2023, 11, 30, 12, 0),
                LocalDateTime.of(2023, 11, 30, 13, 0),
                item1.getId(), BookingStatus.WAITING);

        bookingCollection.add(bookingByUser2);
        bookingCollection.add(bookingOwnerWaiting);
        bookingCollection.add(bookingOwnerRejected);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void testToBookingDtoCollection() {
        Collection<BookingDto> bookingDtoCollection = bookingMapper.toBookingDtoCollection(bookingCollection);
        assertNotNull(bookingDtoCollection);
        assertEquals(3, bookingDtoCollection.size());
        assertEquals(1, bookingDtoCollection.stream().findFirst().get().getId());
    }

    @Test
    void testToBookingDto() {
        BookingDto bookingDto = bookingMapper.toBookingDto(bookingByUser2);
        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.getId());
    }

    @Test
    void testToBooking() {
        Booking booking = bookingMapper.toBooking(bookingDtoIn.getId(), user2, item1, bookingDtoIn);
        assertNotNull(booking);
        assertEquals(1, booking.getId());
    }
}