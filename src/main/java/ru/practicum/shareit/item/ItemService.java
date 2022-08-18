package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:Regexp")
@Slf4j
@Service
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    private int itemId = 0;

    public int generateItemId() {
        return ++itemId;
    }

    public Item createItem(Integer userId, ItemDto itemDto) {
        User owner = userStorage.getUserById(userId);
        validateUser(owner, userId);
        validateItem(itemDto);

        Item item = ItemMapper.toItem(generateItemId(), owner, itemDto);
        return itemStorage.createItem(item);
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

    public void validateUser(User owner, Integer userId) {
        if (userId == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Id пользователя не указан.");
        }
        if (owner == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Пользователя c id " + userId + " нет в базе.");
        }
    }

    public Item updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        User owner = userStorage.getUserById(userId);
        Item itemFromDB = itemStorage.getItemById(itemId);
        Integer ownerIdFromDB = itemFromDB.getOwner().getId();
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
        return itemStorage.updateItem(item);
    }

    public Collection<Item> searchItems(String text) {
        if (!text.isEmpty()) {
            Collection<Item> items = itemStorage.searchItems();
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

    public Item getItemById(int itemId) {
        return itemStorage.getItemById(itemId);
    }

    public Collection<Item> getAllItemsOfUser(Integer userId) {
        return itemStorage.getAllItemsByUser(userId);
    }

    public void deleteItem(Integer itemId) {
        itemStorage.deleteItem(itemId);
    }
}
