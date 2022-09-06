package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.createItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                        @PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен GET запрос к эндпоинту /search?text={}", text);
        return ItemMapper.toItemDtoCollection(itemService.searchItems(text));
    }

    @GetMapping
    Collection<ItemDto> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ItemMapper.toItemDtoCollection(itemService.getAllItemsByUser(userId));
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                       @PathVariable Long itemId,
                       @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    void deleteItem(@RequestHeader(USER_ID_HEADER) int userId,
                    @PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
