package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
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
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту POST/items");
        return ItemMapper.toItemDto(itemService.createItem(userId, itemDto));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                       @PathVariable Long itemId,
                                       @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Получен запрос к эндпоинту POST/items/{}/comment", itemId);
        return ItemMapper.toCommentDto(itemService.createComment(authorId, itemId, commentDtoIn));
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET/items/search?text={}", text);
        final PageRequest pageRequest = findPageRequest(from, size);
        return ItemMapper.toItemDtoCollection(itemService.searchItems(text, pageRequest));
    }

    @GetMapping("/{itemId}")
    public ItemByIdDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту GET/items/{}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemByIdDto> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET/items");
        final PageRequest pageRequest = findPageRequest(from, size);
        return itemService.getAllItemsByUser(userId, pageRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту PATCH/items/{}", itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) int userId,
                           @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту DELETE/items/{}", itemId);
        itemService.deleteItem(itemId);
    }

    public PageRequest findPageRequest(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
