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
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    BookingRepository bookingRepository;

    final PageRequest pageRequest = PageRequest.of(0, 20);

    User user1;
    User user2;

    Item item1;
    Item item2;

    ItemRequest itemRequest;

    //bookings by user1
    Booking bookingByUser1;
    LocalDateTime timeStartBooking1;
    LocalDateTime timeEndBooking1;

    Booking bookingBookerWaitingByUser1;
    LocalDateTime timeStartBookingBookerWaiting;
    LocalDateTime timeEndBookingBookerWaiting;

    Booking bookingBookerRejectedByUser1;
    LocalDateTime timeStartBookingBookerRej;
    LocalDateTime timeEndBookingBookerRej;

    //bookings by user2
    Booking bookingByUser2;
    LocalDateTime timeStartBooking2;
    LocalDateTime timeEndBooking2;

    Booking bookingOwnerWaiting;
    LocalDateTime timeStartBookingOwnerWaiting;
    LocalDateTime timeEndBookingOwnerWaiting;

    Booking bookingOwnerRejected;
    LocalDateTime timeStartBookingOwnerRej;
    LocalDateTime timeEndBookingOwnerRej;

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

    @Test
    void getAllBookingsByUser() {
        Page<Booking> allBookingsByUser = bookingRepository.getAllBookingsByUser(user1.getId(), pageRequest);
        assertNotNull(allBookingsByUser);
        assertEquals(3, allBookingsByUser.getTotalElements());
        assertEquals(timeStartBookingBookerRej, allBookingsByUser.stream().findFirst().get().getStart());
    }

    @Test
    void getCurrentBookingsByUser() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 9, 10, 13, 0);
        Page<Booking> currentBookingsByUser = bookingRepository.getCurrentBookingsByUser(user1.getId(), currentTime,
                                                                                     pageRequest);
        assertNotNull(currentBookingsByUser);
        assertEquals(1, currentBookingsByUser.getTotalElements());
        assertEquals(timeStartBooking1, currentBookingsByUser.stream().findFirst().get().getStart());
    }

    @Test
    void getPastBookingsByUser() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 20, 13, 0);
        Page<Booking> pastBookingsByUser = bookingRepository.getPastBookingsByUser(user1.getId(), currentTime,
                pageRequest);
        assertNotNull(pastBookingsByUser);
        assertEquals(3, pastBookingsByUser.getTotalElements());
        assertEquals(timeStartBookingBookerRej, pastBookingsByUser.stream().findFirst().get().getStart());
    }

    @Test
    void getFutureBookingsByUser() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 7, 20, 13, 0);
        Page<Booking> futureBookingsByUser = bookingRepository.getFutureBookingsByUser(user1.getId(), currentTime,
                pageRequest);
        assertNotNull(futureBookingsByUser);
        assertEquals(3, futureBookingsByUser.getTotalElements());
        assertEquals(timeStartBookingBookerRej, futureBookingsByUser.stream().findFirst().get().getStart());
    }

    @Test
    void getAllBookingsByOwner() {
        Page<Booking> allBookingsByOwner = bookingRepository.getAllBookingsByOwner(user1.getId(), pageRequest);
        assertNotNull(allBookingsByOwner);
        assertEquals(3, allBookingsByOwner.getTotalElements());
        assertEquals(timeStartBookingOwnerRej, allBookingsByOwner.stream().findFirst().get().getStart());
    }

    @Test
    void getCurrentBookingsByOwner() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 10, 10, 13, 0);
        Page<Booking> currentBookingsByOwner = bookingRepository.getCurrentBookingsByOwner(user1.getId(), currentTime,
                pageRequest);
        assertNotNull(currentBookingsByOwner);
        assertEquals(1, currentBookingsByOwner.getTotalElements());
        assertEquals(timeStartBooking2, currentBookingsByOwner.stream().findFirst().get().getStart());
    }

    @Test
    void getPastBookingsByOwner() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 11, 23, 13, 0);
        Page<Booking> pastBookingsByOwner = bookingRepository.getPastBookingsByOwner(user1.getId(), currentTime,
                pageRequest);
        assertNotNull(pastBookingsByOwner);
        assertEquals(3, pastBookingsByOwner.getTotalElements());
        assertEquals(timeStartBookingOwnerRej, pastBookingsByOwner.stream().findFirst().get().getStart());
    }

    @Test
    void getFutureBookingsByOwner() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 8, 22, 12, 0);
        Page<Booking> futureBookingsByOwner = bookingRepository.getFutureBookingsByOwner(user1.getId(), currentTime,
                pageRequest);
        assertNotNull(futureBookingsByOwner);
        assertEquals(3, futureBookingsByOwner.getTotalElements());
        assertEquals(timeStartBookingOwnerRej, futureBookingsByOwner.stream().findFirst().get().getStart());
    }

    @Test
    void getWaitingRejectedBookingsByBooker() {
        BookingStatus bookingStatusWaiting = BookingStatus.WAITING;
        Page<Booking> waitingBookingsByBooker =
                bookingRepository.getWaitingRejectedBookingsByBooker(user1.getId(), bookingStatusWaiting, pageRequest);
        assertNotNull(waitingBookingsByBooker);
        assertEquals(1, waitingBookingsByBooker.getTotalElements());
        assertEquals(timeStartBookingBookerWaiting, waitingBookingsByBooker.stream().findFirst().get().getStart());

        BookingStatus bookingStatusRejected = BookingStatus.REJECTED;
        Page<Booking> rejectedBookingsByBooker =
                bookingRepository.getWaitingRejectedBookingsByBooker(user1.getId(), bookingStatusRejected, pageRequest);
        assertNotNull(rejectedBookingsByBooker);
        assertEquals(1, rejectedBookingsByBooker.getTotalElements());
        assertEquals(timeStartBookingBookerRej, rejectedBookingsByBooker.stream().findFirst().get().getStart());
    }

    @Test
    void getWaitingRejectedBookingsByOwner() {
        BookingStatus bookingStatusWaiting = BookingStatus.WAITING;
        Page<Booking> waitingBookingsByOwner =
                bookingRepository.getWaitingRejectedBookingsByOwner(user1.getId(), bookingStatusWaiting, pageRequest);
        assertNotNull(waitingBookingsByOwner);
        assertEquals(1, waitingBookingsByOwner.getTotalElements());
        assertEquals(timeStartBookingOwnerWaiting, waitingBookingsByOwner.stream().findFirst().get().getStart());

        BookingStatus bookingStatusRejected = BookingStatus.REJECTED;
        Page<Booking> rejectedBookingsByOwner =
                bookingRepository.getWaitingRejectedBookingsByOwner(user1.getId(), bookingStatusRejected, pageRequest);
        assertNotNull(rejectedBookingsByOwner);
        assertEquals(1, rejectedBookingsByOwner.getTotalElements());
        assertEquals(timeStartBookingOwnerRej, rejectedBookingsByOwner.stream().findFirst().get().getStart());
    }

    @Test
    void findLastBookingsByItemId() {
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
    void findNextBookingsByItemId() {
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
    void findBookerByItemId() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 9, 11, 13, 0);
        Booking booking = bookingRepository.findBookerByItemId(item2.getId(), user1.getId(), currentTime);
        assertNotNull(booking);
        assertEquals(2, booking.getItem().getId());
        assertEquals(timeStartBooking1, booking.getStart());
    }
}