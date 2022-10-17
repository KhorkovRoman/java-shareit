package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserDto;
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
@WebMvcTest(controllers = ItemService.class)
@AutoConfigureMockMvc
class ItemServiceTest {

    private ItemService itemService;

    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private ValidationUser validationUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user1;
    private User user2;
    private UserDto userDto;
    private UserDto userDto2;

    private Item item1;
    private ItemDto itemDto;
    private ItemByIdDto itemByIdDto;

    private int from = 0;
    private int size = 20;
    private PageRequest pageRequest;

    private Booking bookingLast;
    private Booking bookingNext;
    private BookingDtoIn bookingDtoInLast;
    private BookingDtoIn bookingDtoInNext;

    private Comment comment1;
    private CommentDtoIn commentDtoIn;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemService(itemRepository, userRepository,
                bookingRepository, commentRepository, itemRequestRepository);
        user1 = new User(1L, "user1", "user1@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        userDto = new UserDto(1L, "user", "user@user.com");
        userDto2 = new UserDto(2L, "user", "user@user.com");

        item1 = new Item(1L, "item 1", "item 1 desciption", true, user1, null);
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

        bookingDtoInLast = new BookingDtoIn(1L,
                LocalDateTime.of(2022, 9, 20, 12, 0),
                LocalDateTime.of(2022, 9, 21, 12, 0),
                item1.getId(), BookingStatus.WAITING);
        bookingDtoInNext = new BookingDtoIn(2L,
                LocalDateTime.of(2023, 9, 20, 12, 0),
                LocalDateTime.of(2023, 9, 21, 12, 0),
                item1.getId(), BookingStatus.WAITING);

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
    void testCreateComment() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findLastBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));
        when(bookingRepository.findNextBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));

        when(bookingRepository.findBookerByItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookingLast);
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(comment1));


        itemService.createComment(user2.getId(), item1.getId(), commentDtoIn);
        log.info("comment = " + comment1);

        Mockito.verify(commentRepository, Mockito.times(1))
                .save(comment1);
        assertNotNull(comment1);
        assertEquals(1, comment1.getId());
    }

    @Test
    void testCreateItem() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        itemService.createItem(user1.getId(), itemDto);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(item1);
        assertNotNull(item1);
        assertEquals(1, item1.getId());
    }

    @Test
    void testUpdateItem() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        itemService.updateItem(user1.getId(), itemDto.getId(), itemDto);
        Mockito.verify(itemRepository, Mockito.times(1))
                .save(item1);
    }

    @Test
    void testSearchItems() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findLastBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));
        when(bookingRepository.findNextBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));
        when(itemRepository.getAllItemsByUser(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(item1)));
        when(itemRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(item1)));

        Collection<Item> items = itemService.searchItems("item 1", pageRequest);

        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void testGetItemById() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findLastBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));
        when(bookingRepository.findNextBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));

        itemByIdDto = itemService.getItemById(item1.getId(), user1.getId());

        assertNotNull(itemByIdDto);
        assertEquals(1, itemByIdDto.getId());
    }

    @Test
    void testGetAllItemsByUser() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findLastBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingLast)));
        when(bookingRepository.findNextBookingsByItemId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(bookingNext)));
        when(itemRepository.getAllItemsByUser(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(item1)));

        Collection<ItemByIdDto> itemByIdDtoList = itemService.getAllItemsByUser(user1.getId(), pageRequest);

        assertNotNull(itemByIdDtoList);
        assertEquals(1, itemByIdDtoList.size());
    }

    @Test
    void testDeleteItem() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemService.createItem(user1.getId(), itemDto))
                .thenReturn(item1);
        itemService.deleteItem(1L);
        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(anyLong());
    }
}