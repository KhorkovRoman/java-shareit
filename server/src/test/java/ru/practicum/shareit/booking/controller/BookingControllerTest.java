package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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

    private BookingController bookingController;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private ItemDto itemDto;
    private ItemByIdDto itemByIdDto;
    private Collection<CommentDtoOut> comments = new ArrayList<>();

    private User user;
    private User user2;
    private Item item1;
    private Comment comment1;
    private int from = 0;
    private int size = 20;
    private PageRequest pageRequest;

    private Booking bookingByUser1;
    private LocalDateTime timeStartBooking1;
    private LocalDateTime timeEndBooking1;

    private BookingDtoIn bookingDtoIn;

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
    public void testCreateBooking() throws Exception {
        when(bookingService.createBooking(any(), any(BookingDtoIn.class)))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .start(timeStartBooking1)
                        .end(timeEndBooking1)
                        .item(item1)
                        .booker(user2)
                        .status(BookingStatus.APPROVED)
                        .build());

        mockMvc.perform(mockAction(post("/bookings"), user2.getId(), bookingDtoIn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoIn.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .createBooking(anyLong(), any(BookingDtoIn.class));
    }

    @Test
    void testApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .start(timeStartBooking1)
                        .end(timeEndBooking1)
                        .item(item1)
                        .booker(user2)
                        .status(BookingStatus.APPROVED)
                        .build());

        mockMvc.perform(mockAction(patch("/bookings/1?approved=true"), user.getId(), bookingDtoIn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoIn.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void testGetBookingById() throws Exception {
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
    void testGetBookingsByUser() throws Exception {
        when(bookingService.getBookingsByUser(anyLong(), any(State.class), any(PageRequest.class)))
                .thenReturn(List.of(bookingByUser1));

        mockMvc.perform(mockAction(get("/bookings/?state=ALL"), user2.getId(), bookingDtoIn))
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
    void testGetBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), any(State.class), any(PageRequest.class)))
                .thenReturn(List.of(bookingByUser1));

        mockMvc.perform(mockAction(get("/bookings/owner?state=ALL"),user.getId(), bookingDtoIn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingByUser1.getId()), Long.class));

        Mockito.verify(bookingService, times(1))
                .getBookingsByOwner(anyLong(), any(State.class), any(PageRequest.class));
    }

    public MockHttpServletRequestBuilder mockAction(MockHttpServletRequestBuilder mockMvc,
                                                    Long userId,
                                                    BookingDtoIn bookingDtoIn)
            throws JsonProcessingException {
        return mockMvc
                .content(mapper.writeValueAsString(bookingDtoIn))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId);
    }

}