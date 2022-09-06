package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

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

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    private Long itemId = 0L;

    public Long generateItemId() {
        return ++itemId;
    }

    public Item createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id " + userId));
        validateUser(owner, userId);
        validateItem(itemDto);

        Item item = ItemMapper.toItem(generateItemId(), owner, itemDto);
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
        Item item = ItemMapper.toItem(itemId, owner, itemDto);
        return itemRepository.save(item);
    }

    public Collection<Item> searchItems(String text) {
        if (!text.isEmpty()) {
            Collection<Item> items = itemRepository.findAll();
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

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "В базе нет предмета c id " + itemId));
    }

    public Collection<Item> getAllItemsByUser(Long userId) {
        return itemRepository.getAllItemsByUser(userId);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
