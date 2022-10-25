package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class ItemMapperTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private Collection<Item> itemCollection = new ArrayList<>();

    private Comment comment1;
    private Comment comment2;

    private Collection<Comment> commentList = new ArrayList<>();

    private ItemRequest itemRequest;

    //bookings by user2
    private Booking bookingByUser2;
    private LocalDateTime timeStartBooking2;
    private LocalDateTime timeEndBooking2;

    private Booking bookingOwnerWaiting;
    private LocalDateTime timeStartBookingOwnerWaiting;
    private LocalDateTime timeEndBookingOwnerWaiting;

    private Booking bookingOwnerRejected;
    private LocalDateTime timeStartBookingOwnerRej;
    private LocalDateTime timeEndBookingOwnerRej;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "User 1 name", "user1@email.ru"));
        itemRequest = itemRequestRepository.save(
                new ItemRequest(1L, "Need Item 2 name", user1, LocalDateTime.now()));
        item1 = itemRepository.save(
                new Item(1L, "item 1", "item 1 desciption", true, user1, null));

        user2 = userRepository.save(new User(2L, "User 2 name", "user2@email.ru"));
        comment1 = commentRepository.save(
                new Comment(1L, "Comment 1", item1, user2,
                        LocalDateTime.of(2023, 10, 20, 12, 0)));
        comment2 = commentRepository.save(
                new Comment(2L, "Comment 2", item1, user2,
                        LocalDateTime.of(2023, 11, 30, 12, 0)));
        commentList.add(comment1);
        commentList.add(comment2);

        item2 = itemRepository.save(
                new Item(2L, "item 2", "item 2 desciption", true, user2, itemRequest));

        itemCollection.add(item1);
        itemCollection.add(item2);

        timeStartBooking2 = LocalDateTime.of(2023, 10, 10, 12, 0);
        timeEndBooking2 = LocalDateTime.of(2023, 10, 11, 12, 0);
        bookingByUser2 = bookingRepository.save(new Booking(4L, timeStartBooking2, timeEndBooking2,
                item1, user2, BookingStatus.APPROVED));

        //bookingByOwnerWaiting
        timeStartBookingOwnerWaiting = LocalDateTime.of(2023, 10, 20, 12, 0);
        timeEndBookingOwnerWaiting = LocalDateTime.of(2023, 10, 21, 12, 0);
        bookingOwnerWaiting = bookingRepository.save(new Booking(5L,
                timeStartBookingOwnerWaiting, timeEndBookingOwnerWaiting,
                item1, user2, BookingStatus.WAITING));

        //bookingByOwnerRejected
        timeStartBookingOwnerRej = LocalDateTime.of(2023, 10, 30, 12, 0);
        timeEndBookingOwnerRej = LocalDateTime.of(2023, 10, 31, 12, 0);
        bookingOwnerRejected = bookingRepository.save(new Booking(6L,
                timeStartBookingOwnerRej, timeEndBookingOwnerRej,
                item1, user2, BookingStatus.REJECTED));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        commentRepository.deleteAll();
        commentList.clear();
    }

    @Test
    void testToCommentDtoCollection() {
        Collection<CommentDtoOut> commentDtoOutCollection = ItemMapper.toCommentDtoCollection(commentList);
        assertNotNull(commentDtoOutCollection);
        assertEquals(2, commentDtoOutCollection.size());
        assertEquals("Comment 1", commentDtoOutCollection.stream().findFirst().get().getText());
    }

    @Test
    void testToCommentDto() {
        CommentDtoOut commentDtoOut = ItemMapper.toCommentDto(comment1);
        assertNotNull(commentDtoOut);
        assertEquals(1, commentDtoOut.getId());
    }

    @Test
    void testToItemDtoCollection() {
        Collection<ItemDto> itemDtoCollection = ItemMapper.toItemDtoCollection(itemCollection);
        assertNotNull(itemDtoCollection);
        assertEquals(2, itemDtoCollection.size());
        assertEquals(1, itemDtoCollection.stream().findFirst().get().getId());
    }

    @Test
    void testToItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        assertNotNull(itemDto);
        assertEquals(1, itemDto.getId());
    }

    @Test
    void testToItemByIdDto() {
        Collection<CommentDtoOut> commentDtoOutCollection = ItemMapper.toCommentDtoCollection(commentList);
        BookingDtoItem bookingDtoByUser2 = BookingMapper.toBookingDtoItem(bookingByUser2);
        BookingDtoItem bookingDtoOwnerWaiting2 = BookingMapper.toBookingDtoItem(bookingOwnerWaiting);
        ItemByIdDto itemByIdDto = ItemMapper.toItemByIdDto(
                item1, bookingDtoByUser2, bookingDtoOwnerWaiting2, commentDtoOutCollection);
        assertNotNull(itemByIdDto);
        assertEquals(1, itemByIdDto.getId());
    }

    @Test
    void testToItem() {
        ItemDto item2Dto = ItemMapper.toItemDto(item2);
        Item item2 = ItemMapper.toItem(item2Dto.getId(), user2, item2Dto, itemRequest);
        assertNotNull(item2);
        assertEquals(2, item2.getId());

    }
}