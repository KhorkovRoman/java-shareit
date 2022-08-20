package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Integer, Item> itemMap = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> searchItems() {
        return itemMap.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Integer itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public Collection<Item> getAllItemsByUser(Integer userId) {
        return itemMap.entrySet()
                .stream()
                .filter(m -> m.getValue().getOwner().getId() == userId)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Integer itemId) {
        itemMap.remove(itemId);
    }
}
