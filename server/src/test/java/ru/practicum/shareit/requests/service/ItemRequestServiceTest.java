package ru.practicum.shareit.requests.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestByIdDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.ValidationUser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@WebMvcTest(controllers = ItemRequestService.class)
@AutoConfigureMockMvc
class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;

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

    private ItemRequest itemRequest;
    private ItemRequestDtoIn itemRequestDtoIn;
    private ItemRequestByIdDto itemRequestByIdDto;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestService(itemRequestRepository, userRepository, itemRepository);
        user1 = new User(1L, "user1", "user1@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        userDto = new UserDto(1L, "user", "user@user.com");
        userDto2 = new UserDto(2L, "user", "user@user.com");

        item1 = new Item(1L, "item 1", "item 1 desciption", true, user1, null);
        itemDto = new ItemDto(1L, "item 1", "item 1 desciption", true, null);
        itemByIdDto = new ItemByIdDto(1L, "item", "description", false,
                null, null, null);
        userRepository.save(user1);
        userRepository.save(user2);

        itemRequest = new ItemRequest(1L, "Need item 1", user2,
                LocalDateTime.now());
        itemRequestDtoIn = new ItemRequestDtoIn(1L, "Need item 1");
        itemRequestByIdDto = new ItemRequestByIdDto(1L, "Need item 1", LocalDateTime.now(), List.of(itemDto));
        itemRequestRepository.save(itemRequest);

        int page = from / size;
        pageRequest = PageRequest.of(page, size);

    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void testCreateItemRequest() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));

        itemRequestService.createItemRequest(user2.getId(), itemRequestDtoIn);

        assertNotNull(itemRequest);
        assertEquals(1, itemRequest.getId());
    }

    @Test
    void testGetItemRequestById() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.getAllItemsByRequest(itemRequest.getId()))
                .thenReturn(List.of(item1));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));

        itemRequestService.getItemRequestById(itemRequest.getId(), user1.getId());

        assertNotNull(itemRequestByIdDto);
        assertEquals(1, itemRequestByIdDto.getId());
    }

    @Test
    void testGetAllItemRequestsByRequester() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRequestRepository.getAllItemRequestsByRequester(user1.getId()))
                .thenReturn(Collections.singletonList(itemRequest));

        Collection<ItemRequestByIdDto> itemByIdDtoList = itemRequestService.getAllItemRequestsByRequester(user1.getId());

        assertNotNull(itemByIdDtoList);
        assertEquals(1, itemByIdDtoList.size());
    }

    @Test
    void testGetAllItemRequestsByPage() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRequestRepository.findAllItemRequests(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(itemRequest)));

        Collection<ItemRequestByIdDto> itemByIdDtoList =
                itemRequestService.getAllItemRequestsByPage(user1.getId(), pageRequest);

        assertNotNull(itemByIdDtoList);
        assertEquals(1, itemByIdDtoList.size());
    }
}