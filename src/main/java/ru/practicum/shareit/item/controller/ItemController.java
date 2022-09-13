package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
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
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.createItem(userId, itemDto));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                @PathVariable Long itemId,
                                @Validated({Update.class}) @RequestBody CommentDtoIn commentDtoIn) {
        return ItemMapper.toCommentDto(itemService.createComment(authorId, itemId, commentDtoIn));
    }

    @GetMapping("/{itemId}")
    public ItemByIdDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен GET запрос к эндпоинту /search?text={}", text);
        return ItemMapper.toItemDtoCollection(itemService.searchItems(text));
    }

    @GetMapping
    public Collection<ItemByIdDto> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getAllItemsByUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                       @PathVariable Long itemId,
                       @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) int userId,
                    @PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
