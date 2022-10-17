package ru.practicum.shareit.requests.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private ItemRequest itemRequest;

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
    void testGetAllItemRequestsByUser() {
        Collection<ItemRequest> allItemRequestsByUser =
                itemRequestRepository.getAllItemRequestsByRequester(user1.getId());
        assertNotNull(allItemRequestsByUser);
        assertEquals(1, allItemRequestsByUser.size());
        assertEquals("Need Item 2 name", allItemRequestsByUser.stream().findFirst().get().getDescription());
    }

    @Test
    void testFindAllItemRequests() {
        final PageRequest pageRequest = PageRequest.of(0, 20);
        Page<ItemRequest> allItemRequests = itemRequestRepository.findAllItemRequests(user2.getId(), pageRequest);
        assertNotNull(allItemRequests);
        assertEquals(1, allItemRequests.getTotalElements());
        assertEquals("Need Item 2 name", allItemRequests.stream().findFirst().get().getDescription());
    }
}