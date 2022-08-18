package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item createItem(Item item);

    Collection<Item> searchItems();

    Item getItemById(Integer itemId);

    Collection<Item> getAllItemsByUser(Integer userId);

    Item updateItem(Item item);

    void deleteItem(Integer itemId);
}
