package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.UnknownStateException;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    BookingController bookingController;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    ItemDto itemDto;
    ItemByIdDto itemByIdDto;
    Collection<CommentDtoOut> comments = new ArrayList<>();

    User user;
    User user2;
    Item item1;
    Comment comment1;
    int from = 0;
    int size = 20;
    PageRequest pageRequest;

    Booking bookingByUser1;
    LocalDateTime timeStartBooking1;
    LocalDateTime timeEndBooking1;

    BookingDtoIn bookingDtoIn;

    @BeforeEach
    void beforeEach() {
        bookingController = new BookingController(bookingService);

        user = new User(1L, "user", "user@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        itemDto = new ItemDto(1L, "item", "description", false, null);
        itemByIdDto = new ItemByIdDto(1L, "item", "description", false,
                null, null, null);
        item1 = new Item(1L, "item 1", "item 1 desciption", true, user, null);
        comment1 = new Comment(1L, "Comment 1", item1, user2,
                LocalDateTime.of(2023, 10, 20, 12, 0));
        int page = from / size;
        pageRequest = PageRequest.of(page, size);

        timeStartBooking1 = LocalDateTime.of(2023, 9, 10, 12, 0);
        timeEndBooking1 = LocalDateTime.of(2023, 9, 11, 12, 0);
        bookingByUser1 = new Booking(1L, timeStartBooking1, timeEndBooking1,
                item1, user2, BookingStatus.WAITING);
        bookingDtoIn = new BookingDtoIn(1L, timeStartBooking1, timeEndBooking1,
                item1.getId(), BookingStatus.WAITING);
    }

    @Test
    public void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(), any(BookingDtoIn.class)))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .start(timeStartBooking1)
                        .end(timeEndBooking1)
                        .item(item1)
                        .booker(user2)
                        .status(BookingStatus.APPROVED)
                        .build());

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoIn.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .createBooking(anyLong(), any(BookingDtoIn.class));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .start(timeStartBooking1)
                        .end(timeEndBooking1)
                        .item(item1)
                        .booker(user2)
                        .status(BookingStatus.APPROVED)
                        .build());

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoIn.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .start(timeStartBooking1)
                        .end(timeEndBooking1)
                        .item(item1)
                        .booker(user2)
                        .status(BookingStatus.APPROVED)
                        .build());

        mockMvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingByUser1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingByUser1.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .getBookingById(anyLong(), anyLong());
    }

    @Test
    void getBookingsByUser() throws Exception {
        when(bookingService.getBookingsByUser(anyLong(), any(State.class), any(PageRequest.class)))
                .thenReturn(List.of(bookingByUser1));

        mockMvc.perform(get("/bookings/?state=ALL")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingByUser1.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .getBookingsByUser(anyLong(), any(State.class), any(PageRequest.class));

        String unknownState = "UnknownState";
        UnknownStateException unknownStateException = Assertions.assertThrows(UnknownStateException.class,
                () -> bookingController.getBookingsByUser(24L, unknownState, from, size));
        assertEquals("Unknown state: UnknownState",
                unknownStateException.getMessage());
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), any(State.class), any(PageRequest.class)))
                .thenReturn(List.of(bookingByUser1));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingByUser1.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .getBookingsByOwner(anyLong(), any(State.class), any(PageRequest.class));
    }
}