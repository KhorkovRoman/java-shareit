package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 20);

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private ItemRequest itemRequest;

    //bookings by user1
    private Booking bookingByUser1;
    private LocalDateTime timeStartBooking1;
    private LocalDateTime timeEndBooking1;

    private Booking bookingBookerWaitingByUser1;
    private LocalDateTime timeStartBookingBookerWaiting;
    private LocalDateTime timeEndBookingBookerWaiting;

    private Booking bookingBookerRejectedByUser1;
    private LocalDateTime timeStartBookingBookerRej;
    private LocalDateTime timeEndBookingBookerRej;

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

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "User 1 name", "user1@email.ru"));
        itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Need Item 2 name", user1, LocalDateTime.now()));
        item1 = itemRepository.save(
                new Item(1L, "item 1", "item 1 desciption", true, user1, null));

        user2 = userRepository.save(new User(2L, "User 2 name", "user2@email.ru"));
        item2 = itemRepository.save(
                new Item(2L, "item 2", "item 2 desciption", true, user2, itemRequest));

        timeStartBooking1 = LocalDateTime.of(2023, 9, 10, 12, 0);
        timeEndBooking1 = LocalDateTime.of(2023, 9, 11, 12, 0);
        bookingByUser1 = bookingRepository.save(new Booking(1L, timeStartBooking1, timeEndBooking1,
                 item2, user1, BookingStatus.APPROVED));
        //bookingByBookerWaiting
        timeStartBookingBookerWaiting = LocalDateTime.of(2023, 9, 20, 12, 0);
        timeEndBookingBookerWaiting = LocalDateTime.of(2023, 9, 21, 12, 0);
        bookingBookerWaitingByUser1 = bookingRepository.save(new Booking(2L,
                timeStartBookingBookerWaiting, timeEndBookingBookerWaiting,
                item2, user1, BookingStatus.WAITING));
        //bookingByBookerRejected
        timeStartBookingBookerRej = LocalDateTime.of(2023, 9, 29, 12, 0);
        timeEndBookingBookerRej = LocalDateTime.of(2023, 9, 30, 12, 0);
        bookingBookerRejectedByUser1 = bookingRepository.save(new Booking(3L,
                timeStartBookingBookerRej, timeEndBookingBookerRej,
                item2, user1, BookingStatus.REJECTED));

        timeStartBooking2 = LocalDateTime.of(2023, 10, 10, 12, 0);
        timeEndBooking2 = LocalDateTime.of(2023, 10, 11, 12, 0);
        bookingByUser2 = bookingRepository.save(new Booking(4L, timeStartBooking2, timeEndBooking2,
                item1, user2, BookingStatus.APPROVED));

        //bookingByOwnerWaiting
        timeStartBookingOwnerWaiting = LocalDateTime.of(2023, 10, 20, 12, 0);
        timeEndBookingOwnerWaiting = LocalDateTime.of(2023, 10, 21, 12, 0);
        bookingOwnerWaiting = bookingRepository.save(new Booking(5L,
                timeStartBookingOwnerWaiting, timeEndBookingOwnerWaiting,
                item1, user2, BookingStatus.WAITING));

        //bookingByOwnerRejected
        timeStartBookingOwnerRej = LocalDateTime.of(2023, 10, 30, 12, 0);
        timeEndBookingOwnerRej = LocalDateTime.of(2023, 10, 31, 12, 0);
        bookingOwnerRejected = bookingRepository.save(new Booking(6L,
                timeStartBookingOwnerRej, timeEndBookingOwnerRej,
                item1, user2, BookingStatus.REJECTED));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    void assertCommonTest(Page<Booking> bookings, LocalDateTime timeStartBooking, int count) {
        assertNotNull(bookings);
        assertEquals(count, bookings.getTotalElements());
        assertEquals(timeStartBooking, bookings.stream().findFirst().get().getStart());
    }

    @Test
    void testGetAllBookingsByUser() {
        Page<Booking> allBookingsByUser = bookingRepository.getAllBookingsByUser(user1.getId(), pageRequest);
        assertCommonTest(allBookingsByUser, timeStartBookingBookerRej, 3);
    }

    @Test
    void testGetCurrentBookingsByUser() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 9, 10, 13, 0);
        Page<Booking> currentBookingsByUser = bookingRepository.getCurrentBookingsByUser(user1.getId(), currentTime,
                                             pageRequest);
        assertCommonTest(currentBookingsByUser, timeStartBooking1, 1);
    }

    @Test
    void testGetPastBookingsByUser() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 20, 13, 0);
        Page<Booking> pastBookingsByUser = bookingRepository.getPastBookingsByUser(user1.getId(), currentTime,
                pageRequest);
        assertCommonTest(pastBookingsByUser, timeStartBookingBookerRej, 3);
    }

    @Test
    void testGetFutureBookingsByUser() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 7, 20, 13, 0);
        Page<Booking> futureBookingsByUser = bookingRepository.getFutureBookingsByUser(user1.getId(), currentTime,
                pageRequest);
        assertCommonTest(futureBookingsByUser, timeStartBookingBookerRej, 3);
    }

    @Test
    void testGetAllBookingsByOwner() {
        Page<Booking> allBookingsByOwner = bookingRepository.getAllBookingsByOwner(user1.getId(), pageRequest);
        assertCommonTest(allBookingsByOwner, timeStartBookingOwnerRej, 3);
    }

    @Test
    void testGetCurrentBookingsByOwner() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 10, 10, 13, 0);
        Page<Booking> currentBookingsByOwner = bookingRepository.getCurrentBookingsByOwner(user1.getId(), currentTime,
                pageRequest);
        assertCommonTest(currentBookingsByOwner, timeStartBooking2, 1);
    }

    @Test
    void testGetPastBookingsByOwner() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 11, 23, 13, 0);
        Page<Booking> pastBookingsByOwner = bookingRepository.getPastBookingsByOwner(user1.getId(), currentTime,
                pageRequest);
        assertCommonTest(pastBookingsByOwner, timeStartBookingOwnerRej, 3);
    }

    @Test
    void testGetFutureBookingsByOwner() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 8, 22, 12, 0);
        Page<Booking> futureBookingsByOwner = bookingRepository.getFutureBookingsByOwner(user1.getId(), currentTime,
                pageRequest);
        assertCommonTest(futureBookingsByOwner, timeStartBookingOwnerRej, 3);
    }

    @Test
    void testGetWaitingRejectedBookingsByBooker() {
        BookingStatus bookingStatusWaiting = BookingStatus.WAITING;
        Page<Booking> waitingBookingsByBooker =
                bookingRepository.getWaitingRejectedBookingsByBooker(user1.getId(), bookingStatusWaiting, pageRequest);
        assertCommonTest(waitingBookingsByBooker, timeStartBookingBookerWaiting, 1);

        BookingStatus bookingStatusRejected = BookingStatus.REJECTED;
        Page<Booking> rejectedBookingsByBooker =
                bookingRepository.getWaitingRejectedBookingsByBooker(user1.getId(), bookingStatusRejected, pageRequest);
        assertCommonTest(rejectedBookingsByBooker, timeStartBookingBookerRej, 1);
    }

    @Test
    void testGetWaitingRejectedBookingsByOwner() {
        BookingStatus bookingStatusWaiting = BookingStatus.WAITING;
        Page<Booking> waitingBookingsByOwner =
                bookingRepository.getWaitingRejectedBookingsByOwner(user1.getId(), bookingStatusWaiting, pageRequest);
        assertCommonTest(waitingBookingsByOwner, timeStartBookingOwnerWaiting, 1);

        BookingStatus bookingStatusRejected = BookingStatus.REJECTED;
        Page<Booking> rejectedBookingsByOwner =
                bookingRepository.getWaitingRejectedBookingsByOwner(user1.getId(), bookingStatusRejected, pageRequest);
        assertCommonTest(rejectedBookingsByOwner, timeStartBookingOwnerRej, 1);
    }

    @Test
    void testFindLastBookingsByItemId() {
        PageRequest pageRequestForLastBooking =
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime currentTime = LocalDateTime.of(2023, 9, 20, 13, 0);
        Page<Booking> lastBookingsByItemId = bookingRepository.findLastBookingsByItemId(item2.getId(), currentTime,
                                                                                  pageRequestForLastBooking);
        assertNotNull(lastBookingsByItemId);
        assertEquals(2, lastBookingsByItemId.stream().findFirst().get().getItem().getId());
        assertEquals(timeStartBookingBookerWaiting, lastBookingsByItemId.stream().findFirst().get().getStart());
    }

    @Test
    void testFindNextBookingsByItemId() {
        PageRequest pageRequestForNextBooking =
                PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "start"));
        LocalDateTime currentTime = LocalDateTime.of(2023, 9, 10, 13, 0);
        Page<Booking> nextBookingsByItemId = bookingRepository.findNextBookingsByItemId(item2.getId(), currentTime,
                                                                                  pageRequestForNextBooking);
        assertNotNull(nextBookingsByItemId);
        assertEquals(2, nextBookingsByItemId.stream().findFirst().get().getItem().getId());
        assertEquals(timeStartBookingBookerWaiting, nextBookingsByItemId.stream().findFirst().get().getStart());
    }

    @Test
    void testFindBookerByItemId() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 9, 11, 13, 0);
        Booking booking = bookingRepository.findBookerByItemId(item2.getId(), user1.getId(), currentTime);
        assertNotNull(booking);
        assertEquals(2, booking.getItem().getId());
        assertEquals(timeStartBooking1, booking.getStart());
    }
}