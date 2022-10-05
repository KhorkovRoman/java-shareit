package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.ValidationUser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@WebMvcTest(controllers = BookingService.class)
@AutoConfigureMockMvc
class BookingServiceTest {

    BookingService bookingService;

    @MockBean
    ItemService itemService;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    ItemRequestRepository itemRequestRepository;
    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ValidationUser validationUser;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    User user1;
    User user2;
    User user3;

    UserDto userDto;
    UserDto userDto2;

    Item item1;
    Item item2;
    Item itemNotAvailable;

    ItemDto itemDto;
    ItemByIdDto itemByIdDto;

    int from = 0;
    int size = 20;
    PageRequest pageRequest;

    Booking booking;
    Booking bookingLast;
    Booking bookingNext;
    Booking bookingNext3;
    Booking bookingNextRejected;
    Booking bookingCurrent;
    Booking bookingRejected;
    Booking bookingEndThenStart;
    BookingDtoIn bookingDtoInEndThenStart;
    BookingDtoIn bookingDtoInNoItem;
    BookingDtoIn bookingDtoInStartBeforeNow;
    BookingDtoIn bookingDtoInEndBeforeNow;
    BookingDtoIn bookingDtoInItemNotAvailable;

    BookingDtoIn bookingDtoInLast;
    BookingDtoIn bookingDtoInNext;

    Comment comment1;
    CommentDtoIn commentDtoIn;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingService(bookingRepository, userRepository, itemRepository);
        user1 = new User(1L, "user1", "user1@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        user3 = new User(3L, "user3", "user3@user.com");
        userDto = new UserDto(1L, "user", "user@user.com");
        userDto2 = new UserDto(2L, "user", "user@user.com");

        item1 = new Item(1L, "item 1", "item 1 desciption", true, user1, null);
        item2 = new Item(2L, "item 2", "item 2 desciption", true, user2, null);
        itemNotAvailable = new Item(3L, "item 3", "item 3 desciption", false, user1, null);

        itemDto = new ItemDto(1L, "item 1", "item 1 desciption", true, null);
        itemByIdDto = new ItemByIdDto(1L, "item", "description", false,
                null, null, null);
        userRepository.save(user1);

        int page = from / size;
        pageRequest = PageRequest.of(page, size);

        bookingLast = new Booking(1L,
                LocalDateTime.of(2022, 9, 20, 12, 0),
                LocalDateTime.of(2022, 9, 21, 12, 0),
                item1, user2, BookingStatus.WAITING);

        bookingNext = new Booking(2L,
                LocalDateTime.of(2023, 9, 20, 12, 0),
                LocalDateTime.of(2023, 9, 21, 12, 0),
                item1, user2, BookingStatus.WAITING);

        bookingNext3 = new Booking(3L,
                LocalDateTime.of(2023, 10, 20, 12, 0),
                LocalDateTime.of(2023, 10, 21, 12, 0),
                item1, user2, BookingStatus.WAITING);

        bookingCurrent = new Booking(3L,
                LocalDateTime.of(2021, 10, 20, 12, 0),
                LocalDateTime.of(2023, 10, 21, 12, 0),
                item1, user2, BookingStatus.WAITING);

        bookingNextRejected = new Booking(15L,
                LocalDateTime.of(2023, 10, 20, 12, 0),
                LocalDateTime.of(2023, 10, 21, 12, 0),
                item1, user2, BookingStatus.WAITING);

        bookingRejected = new Booking(3L,
                LocalDateTime.of(2023, 1, 20, 12, 0),
                LocalDateTime.of(2023, 1, 21, 12, 0),
                item1, user2, BookingStatus.REJECTED);

        bookingEndThenStart = new Booking(3L,
                LocalDateTime.of(2023, 1, 21, 12, 0),
                LocalDateTime.of(2023, 1, 20, 12, 0),
                item1, user2, BookingStatus.REJECTED);

        bookingDtoInEndThenStart = new BookingDtoIn(3L,
                LocalDateTime.of(2023, 1, 21, 12, 0),
                LocalDateTime.of(2023, 1, 20, 12, 0),
                item1.getId(), BookingStatus.REJECTED);
        bookingDtoInStartBeforeNow = new BookingDtoIn(4L,
                LocalDateTime.of(2021, 1, 21, 12, 0),
                LocalDateTime.of(2023, 1, 20, 12, 0),
                item1.getId(), BookingStatus.REJECTED);
        bookingDtoInEndBeforeNow = new BookingDtoIn(5L,
                LocalDateTime.of(2021, 1, 21, 12, 0),
                LocalDateTime.of(2021, 1, 22, 12, 0),
                item1.getId(), BookingStatus.REJECTED);
        bookingDtoInItemNotAvailable = new BookingDtoIn(6L,
                LocalDateTime.of(2023, 1, 21, 12, 0),
                LocalDateTime.of(2023, 1, 22, 12, 0),
                itemNotAvailable.getId(), BookingStatus.REJECTED);

        bookingDtoInLast = new BookingDtoIn(1L,
                LocalDateTime.of(2022, 9, 20, 12, 0),
                LocalDateTime.of(2022, 9, 21, 12, 0),
                item1.getId(), BookingStatus.WAITING);
        bookingDtoInNext = new BookingDtoIn(2L,
                LocalDateTime.of(2023, 9, 20, 12, 0),
                LocalDateTime.of(2023, 9, 21, 12, 0),
                item1.getId(), BookingStatus.WAITING);

        bookingDtoInNoItem = new BookingDtoIn(4L,
                LocalDateTime.of(2023, 8, 20, 12, 0),
                LocalDateTime.of(2023, 8, 21, 12, 0),
                32L, BookingStatus.WAITING);

        bookingRepository.save(bookingLast);
        bookingRepository.save(bookingNext);

        comment1 = new Comment(1L, "Comment 1", item1, user2,
                LocalDateTime.of(2023, 10, 20, 12, 0));
        commentDtoIn = new CommentDtoIn(1L, "Comment DtoIn 1");
        commentRepository.save(comment1);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void createBooking() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findById(bookingNext.getId()))
                .thenReturn(Optional.ofNullable(bookingNext));

        bookingService.createBooking(user2.getId(), bookingDtoInNext);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(bookingNext);

        ValidationException validationExceptionEndBeforeStart = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2.getId(), bookingDtoInEndThenStart));
        assertEquals("400 BAD_REQUEST \"Конец брони раньше начала.\"",
                validationExceptionEndBeforeStart.getMessage());

        ValidationException validationExceptionEndBeforeNow = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2.getId(), bookingDtoInEndBeforeNow));
        assertEquals("400 BAD_REQUEST \"Конец брони раньше текущего времени.\"",
                validationExceptionEndBeforeNow.getMessage());

        ValidationException validationExceptionStartBeforeNow = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2.getId(), bookingDtoInStartBeforeNow));
        assertEquals("400 BAD_REQUEST \"Начало брони раньше текущего времени.\"",
                validationExceptionStartBeforeNow.getMessage());

        ValidationException validationExceptionNoUser = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(24L, bookingDtoInNext));
        assertEquals("404 NOT_FOUND \"Не найден пользователь с id 24\"",
                validationExceptionNoUser.getMessage());

        ValidationException validationExceptionNoItem = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2.getId(), bookingDtoInNoItem));
        assertEquals("404 NOT_FOUND \"Не найден предмет с id 32\"",
                validationExceptionNoItem.getMessage());

        when(itemRepository.findById(itemNotAvailable.getId()))
                .thenReturn(Optional.ofNullable(itemNotAvailable));
        ValidationException validationExceptionItemNotAvailable = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2.getId(), bookingDtoInItemNotAvailable));
        assertEquals("400 BAD_REQUEST \"Предмет не доступен.\"",
                validationExceptionItemNotAvailable.getMessage());

        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        ValidationException validationExceptionOwner = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(1L, bookingDtoInNext));
        assertEquals("404 NOT_FOUND \"Владелец предмета не может его бронировать.\"",
                validationExceptionOwner.getMessage());

        assertNotNull(bookingNext);
        assertEquals(2, bookingNext.getId());
    }

    @Test
    void approveBooking() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findById(bookingNext3.getId()))
                .thenReturn(Optional.ofNullable(bookingNext3));

        bookingService.approveBooking(user1.getId(), bookingNext3.getId(), true);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(bookingNext3);
        assertNotNull(bookingNext3);
        assertEquals(3, bookingNext3.getId());

        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findById(bookingNextRejected.getId()))
                .thenReturn(Optional.ofNullable(bookingNextRejected));

        bookingService.approveBooking(user1.getId(), bookingNextRejected.getId(), false);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(bookingNextRejected);
        assertNotNull(bookingNextRejected);
        assertEquals(15, bookingNextRejected.getId());

        ValidationException validationExceptionNoBooking = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(user2.getId(), 32L, true));
        assertEquals("404 NOT_FOUND \"Не найдено бронирование с id 32\"",
                validationExceptionNoBooking.getMessage());

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        ValidationException validationExceptionUserNotOwner = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(user2.getId(), bookingNext3.getId(), true));
        assertEquals("404 NOT_FOUND \"Пользователь с id 2 не является хозяином вещи.\"",
                validationExceptionUserNotOwner.getMessage());

        ValidationException validationExceptionBookingAlreadyApproved = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(user1.getId(), bookingNext3.getId(), true));
        assertEquals("400 BAD_REQUEST \"Бронирование с id 3 уже подтверждено.\"",
                validationExceptionBookingAlreadyApproved.getMessage());

        when(bookingRepository.findById(bookingRejected.getId()))
                .thenReturn(Optional.ofNullable(bookingRejected));
        ValidationException validationExceptionBookingAlreadyRejected = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(user1.getId(), bookingRejected.getId(), false));
        assertEquals("400 BAD_REQUEST \"Бронирование с id 3 уже отменено.\"",
                validationExceptionBookingAlreadyRejected.getMessage());
    }

    @Test
    void getBookingById() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findById(bookingLast.getId()))
                .thenReturn(Optional.ofNullable(bookingLast));

        booking = bookingService.getBookingById(user2.getId(), bookingLast.getId());

        assertNotNull(booking);
        assertEquals(1, booking.getId());

        ValidationException validationExceptionNoBooking = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(2L, 63L));
        assertEquals("404 NOT_FOUND \"Не найдено бронирование с id 63\"",
                validationExceptionNoBooking.getMessage());

        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        ValidationException validationExceptionUserNotBookerOrOwner = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(user3.getId(), bookingLast.getId()));
        assertEquals("404 NOT_FOUND \"Пользователь с id 3 не является хозяином или бронирующим вещи.\"",
                validationExceptionUserNotBookerOrOwner.getMessage());
    }

    @Test
    void getBookingsByUser() {
        //ALL
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getAllBookingsByUser(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));

        Collection<Booking> bookings = bookingService.getBookingsByUser(user2.getId(), State.ALL, pageRequest);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());

        //CURRENT
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getCurrentBookingsByUser(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingCurrent)));

        Collection<Booking> bookingsCurrent = bookingService.getBookingsByUser(user2.getId(), State.CURRENT, pageRequest);

        assertNotNull(bookingsCurrent);
        assertEquals(1, bookingsCurrent.size());

        //PAST
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getPastBookingsByUser(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));

        Collection<Booking> bookingsPast = bookingService.getBookingsByUser(user2.getId(), State.PAST, pageRequest);

        assertNotNull(bookingsPast);
        assertEquals(1, bookingsPast.size());

        //FUTURE
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getFutureBookingsByUser(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));

        Collection<Booking> bookingsFuture = bookingService.getBookingsByUser(user2.getId(), State.FUTURE, pageRequest);

        assertNotNull(bookingsFuture);
        assertEquals(1, bookingsFuture.size());

        //WAITING
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getWaitingRejectedBookingsByBooker(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));

        Collection<Booking> bookingsWaiting = bookingService.getBookingsByUser(user2.getId(), State.WAITING, pageRequest);

        assertNotNull(bookingsWaiting);
        assertEquals(1, bookingsWaiting.size());

        //REJECTED
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getWaitingRejectedBookingsByBooker(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingRejected)));

        Collection<Booking> bookingsRejected = bookingService.getBookingsByUser(user2.getId(), State.REJECTED, pageRequest);

        assertNotNull(bookingsRejected);
        assertEquals(1, bookingsRejected.size());

        ValidationException validationExceptionNoUser = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByUser(24L, State.REJECTED, pageRequest));
        assertEquals("404 NOT_FOUND \"Не найден пользователь с id 24\"",
                validationExceptionNoUser.getMessage());
    }

    @Test
    void getBookingsByOwner() {
        //ALL
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getAllBookingsByOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));

        Collection<Booking> bookings = bookingService.getBookingsByOwner(user1.getId(), State.ALL, pageRequest);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());

        //CURRENT
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getCurrentBookingsByOwner(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingCurrent)));

        Collection<Booking> bookingsCurrent = bookingService.getBookingsByOwner(user1.getId(), State.CURRENT, pageRequest);

        assertNotNull(bookingsCurrent);
        assertEquals(1, bookingsCurrent.size());

        //PAST
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getPastBookingsByOwner(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));

        Collection<Booking> bookingsPast = bookingService.getBookingsByOwner(user1.getId(), State.PAST, pageRequest);

        assertNotNull(bookingsPast);
        assertEquals(1, bookingsPast.size());

        //FUTURE
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getFutureBookingsByOwner(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));

        Collection<Booking> bookingsFuture = bookingService.getBookingsByOwner(user1.getId(), State.FUTURE, pageRequest);

        assertNotNull(bookingsFuture);
        assertEquals(1, bookingsFuture.size());

        //WAITING
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getWaitingRejectedBookingsByOwner(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));

        Collection<Booking> bookingsWaiting = bookingService.getBookingsByOwner(user1.getId(), State.WAITING, pageRequest);

        assertNotNull(bookingsWaiting);
        assertEquals(1, bookingsWaiting.size());

        //REJECTED
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getWaitingRejectedBookingsByOwner(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingRejected)));

        Collection<Booking> bookingsRejected = bookingService.getBookingsByOwner(user1.getId(), State.REJECTED, pageRequest);

        assertNotNull(bookingsRejected);
        assertEquals(1, bookingsRejected.size());

        ValidationException validationExceptionNoUser = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByOwner(24L, State.REJECTED, pageRequest));
        assertEquals("404 NOT_FOUND \"Не найден пользователь с id 24\"",
                validationExceptionNoUser.getMessage());
    }
}