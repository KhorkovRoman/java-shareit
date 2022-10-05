package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestByIdDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    ItemRequest itemRequest;
    ItemRequestDtoIn itemRequestDtoIn;
    ItemRequestByIdDto itemRequestByIdDto;

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

    @BeforeEach
    void beforeEach() {
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

        itemRequest = new ItemRequest(1L, "Need Item", user2, LocalDateTime.now());
        itemRequestDtoIn = new ItemRequestDtoIn(1L, "Need Item Dto In");
        itemRequestByIdDto = new ItemRequestByIdDto(1L, "Need Item", LocalDateTime.now(), List.of(itemDto));
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(), any(ItemRequestDtoIn.class)))
                .thenReturn(ItemRequest.builder()
                        .id(1L)
                        .description("Need Item")
                        .requester(user2)
                        .created(LocalDateTime.now())
                        .build());

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription()), String.class));

        Mockito.verify(itemRequestService, times(1))
                .createItemRequest(anyLong(), any(ItemRequestDtoIn.class));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(ItemRequestByIdDto.builder()
                        .id(1L)
                        .description("description")
                        .created(LocalDateTime.now())
                        .items(List.of(itemDto))
                        .build());

        mockMvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        Mockito.verify(itemRequestService, times(1))
                .getItemRequestById(anyLong(), anyLong());
    }

    @Test
    void getAllItemRequestsByUser() throws Exception {
        when(itemRequestService.getAllItemRequestsByRequester(anyLong()))
                .thenReturn(List.of(itemRequestByIdDto));

        mockMvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestByIdDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestByIdDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestByIdDto.getDescription())));

        Mockito.verify(itemRequestService, times(1))
                .getAllItemRequestsByRequester(anyLong());
    }

    @Test
    void getAllItemRequestsByPage() throws Exception {
        when(itemRequestService.getAllItemRequestsByPage(user.getId(), pageRequest))
                .thenReturn(Collections.singletonList(itemRequestByIdDto));

        mockMvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestByIdDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestByIdDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestByIdDto.getDescription())));

        Mockito.verify(itemRequestService, times(1))
                .getAllItemRequestsByPage(anyLong(), any(PageRequest.class));
    }
}