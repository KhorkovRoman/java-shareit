package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

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

    ItemRequest itemRequest;

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
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void getAllItemsByUser() {
        final PageRequest pageRequest = PageRequest.of(0, 20);
        Page<Item> allItemsByUser = itemRepository.getAllItemsByUser(user1.getId(), pageRequest);
        assertNotNull(allItemsByUser);
        assertEquals(1, allItemsByUser.getTotalElements());
        assertEquals("item 1", allItemsByUser.get().findFirst().get().getName());
    }

    @Test
    void getAllItemsByRequest() {
        Collection<Item> allItemsByRequest = itemRepository.getAllItemsByRequest(itemRequest.getId());
        assertNotNull(allItemsByRequest);
        assertEquals(1, allItemsByRequest.size());
        assertEquals("item 2", allItemsByRequest.stream().findFirst().get().getName());
    }
}






