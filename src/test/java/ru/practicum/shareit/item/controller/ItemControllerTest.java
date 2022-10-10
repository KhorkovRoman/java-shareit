package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

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
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(any(), any(ItemDto.class)))
                .thenReturn(Item.builder().id(1L).name("item").description("description").build());

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        Mockito.verify(itemService, times(1))
                        .createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.createComment(any(), any(), any(CommentDtoIn.class)))
                .thenReturn(Comment.builder()
                        .id(1L)
                        .text("Comment 1")
                        .item(item1)
                        .author(user2)
                        .created(LocalDateTime.of(2023, 10, 20, 12, 0))
                        .build());

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment1.getText())));

        Mockito.verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDtoIn.class));

    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(ItemByIdDto.builder()
                        .id(1L)
                        .name("item")
                        .description("description")
                        .build());

        mockMvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        Mockito.verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.searchItems("item 1", pageRequest))
                .thenReturn(List.of(item1));

        mockMvc.perform(mockAction(get("/items/search?text=item 1"), user.getId(), item1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())));

        Mockito.verify(itemService, times(1))
                .searchItems(anyString(), any(PageRequest.class));
    }

    @Test
    void testGetAllItems() throws Exception {
        when(itemService.getAllItemsByUser(user.getId(), pageRequest))
                .thenReturn(Collections.singletonList(itemByIdDto));

        mockMvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemByIdDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemByIdDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemByIdDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemByIdDto.getDescription())));

        Mockito.verify(itemService, times(1))
                .getAllItemsByUser(anyLong(), any(PageRequest.class));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(Item.builder().id(1L).name("item").description("description").build());

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        Mockito.verify(itemService, times(1))
                .updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(mockAction(delete("/items/1"), user.getId(), item1))
                .andExpect(status().isOk());

        Mockito.verify(itemService, times(1))
                .deleteItem(anyLong());
    }

    public MockHttpServletRequestBuilder mockAction(MockHttpServletRequestBuilder mockMvc, Long userId, Item item)
            throws JsonProcessingException {
        return mockMvc
                .content(mapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId);
    }
}


















