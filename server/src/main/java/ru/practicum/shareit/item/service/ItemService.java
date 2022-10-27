package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:Regexp")
@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       BookingRepository bookingRepository, CommentRepository commentRepository,
                       ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    private Long itemId = 0L;

    public Long generateItemId() {
        return ++itemId;
    }

    private Long commentId = 0L;

    public Long generateCommentId() {
        return ++commentId;
    }

    public Comment createComment(Long authorId, Long itemId, CommentDtoIn commentDtoIn) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id " + authorId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Не найден предмет с id " + itemId));
        LocalDateTime dateTimeNow = LocalDateTime.now();
        validateComment(authorId, itemId, commentDtoIn, dateTimeNow);
        Comment comment = new Comment(generateCommentId(), commentDtoIn.getText(), item, author, dateTimeNow);
        return commentRepository.save(comment);
    }

    public void validateComment(Long authorId, Long itemId, CommentDtoIn commentDtoIn, LocalDateTime dateTimeNow) {
        if (commentDtoIn.getText().isEmpty()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Текст комментария пустой.");
        }

        Booking booking = bookingRepository.findBookerByItemId(itemId, authorId, dateTimeNow);
        if (booking == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Автор комментария не арендовал предмет.");
        }
    }

    public Item createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id " + userId));
        validateUser(owner, userId);
        validateItem(itemDto);
        Long itemRequestId = itemDto.getRequestId();

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemRequestId)
                    .orElseThrow(() -> new RuntimeException("Не найден запрос с id " + itemRequestId));
        }
        Item item = ItemMapper.toItem(generateItemId(), owner, itemDto, itemRequest);
        return itemRepository.save(item);
    }

    public void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Не указано название.");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Не указано описание.");
        }
    }

    public void validateUser(User owner, Long userId) {
        if (userId == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Id пользователя не указан.");
        }
        if (owner == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "В базе нет пользователя c id " + userId);
        }
    }

    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id " + userId));
        Item itemFromDB = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Не найден предмет с id " + itemId));
        Long ownerIdFromDB = itemFromDB.getOwner().getId();

        validateUser(owner, userId);
        if (!Objects.equals(ownerIdFromDB, userId)) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Пользователь c id " + userId + " не хозяин вещи.");
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemFromDB.getAvailable());
        }
        if (itemDto.getName() == null) {
            itemDto.setName(itemFromDB.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemFromDB.getDescription());
        }

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            Long itemRequestId = itemDto.getRequestId();
            itemRequest = itemRequestRepository.findById(itemRequestId)
                    .orElseThrow(() -> new RuntimeException("Не найден запрос с id " + itemRequestId));
        }

        Item item = ItemMapper.toItem(itemId, owner, itemDto, itemRequest);
        return itemRepository.save(item);
    }

    public Collection<Item> searchItems(String text, PageRequest pageRequest) {
        if (!text.isEmpty()) {
            Page<Item> items = itemRepository.findAll(pageRequest);
            return items.stream()
                    .filter(item -> isContain(item.getName(), text) || isContain(item.getDescription(), text))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private boolean isContain(String line, String text) {
        return line.toLowerCase().contains(text.toLowerCase());
    }

    public ItemByIdDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
               .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                       "В базе нет предмета c id " + itemId));
        LocalDateTime dateTimeNow = LocalDateTime.now();
        PageRequest pageRequestForLastBooking =
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> lastBooking = bookingRepository.findLastBookingsByItemId(itemId, dateTimeNow, pageRequestForLastBooking);
        BookingDtoItem lastBookingDto = null;
        if (!lastBooking.isEmpty()) {
            lastBookingDto = BookingMapper.toBookingDtoItem(lastBooking.stream().findFirst().get());
        }

        PageRequest pageRequestForNextBooking =
                PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> nextBooking = bookingRepository.findNextBookingsByItemId(itemId, dateTimeNow, pageRequestForNextBooking);
        BookingDtoItem nextBookingDto = null;
        if (!nextBooking.isEmpty()) {
            nextBookingDto = BookingMapper.toBookingDtoItem(nextBooking.stream().findFirst().get());
        }

        Collection<CommentDtoOut> commentList =
                ItemMapper.toCommentDtoCollection(commentRepository.getAllCommentsByItem(itemId));

        if (Objects.equals(userId, item.getOwner().getId())) {
            return ItemMapper.toItemByIdDto(item, lastBookingDto, nextBookingDto, commentList);
        } else {
            return ItemMapper.toItemByIdDto(item, null, null, commentList);
        }
    }

    public Collection<ItemByIdDto> getAllItemsByUser(Long userId, PageRequest pageRequest) {

        Page<Item> itemCollection = itemRepository.getAllItemsByUser(userId, pageRequest);
        return itemCollection.stream()
                .map(item -> getItemById(item.getId(), userId))
                .collect(Collectors.toList());
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
