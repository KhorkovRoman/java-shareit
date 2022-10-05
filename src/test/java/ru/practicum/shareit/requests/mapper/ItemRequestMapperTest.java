package ru.practicum.shareit.requests.mapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestByIdDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;
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
class ItemRequestMapperTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;

    Item item1;
    Item item2;

    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    ItemRequestDtoIn itemRequestDtoIn;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "User 1 name", "user1@email.ru"));
        user2 = userRepository.save(new User(2L, "User 2 name", "user2@email.ru"));

        itemRequest1 = itemRequestRepository.save(
                new ItemRequest(1L, "Need Item 2 name", user1, LocalDateTime.now()));
        itemRequest2 = itemRequestRepository.save(
                new ItemRequest(2L, "Need Item 1 name", user1, LocalDateTime.now()));
        itemRequestDtoIn = new ItemRequestDtoIn(3L, "Need Item 3 name");

        item1 = itemRepository.save(
                new Item(1L, "item 1", "item 1 desciption", true, user1, itemRequest2));
        item2 = itemRepository.save(
                new Item(2L, "item 2", "item 2 desciption", true, user2, itemRequest1));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void toItemRequestByIdDto() {
        Collection<ItemDto> itemDtoCollection = new ArrayList<>();
        ItemDto item1Dto = ItemMapper.toItemDto(item1);
        itemDtoCollection.add(item1Dto);
        ItemRequestByIdDto itemRequestByIdDto = ItemRequestMapper.toItemRequestByIdDto(itemRequest2, itemDtoCollection);
        assertNotNull(itemRequestByIdDto);
        assertEquals(2, itemRequestByIdDto.getId());
    }

    @Test
    void toItemRequestDto() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest1);
        assertNotNull(itemRequestDto);
        assertEquals(1, itemRequestDto.getId());
    }

    @Test
    void toItemRequest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(
                itemRequestDtoIn.getId(), user1, itemRequestDtoIn, LocalDateTime.now());
        assertNotNull(itemRequest);
        assertEquals(3, itemRequest.getId());
        assertEquals(1, itemRequest.getRequester().getId());
    }
}