package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Service
public class ItemMapper {
    public static Collection<ItemDto> toItemDtoCollection(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();

        for (Item item: items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
                //item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(int itemId, User owner, ItemDto itemDto) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }
}
