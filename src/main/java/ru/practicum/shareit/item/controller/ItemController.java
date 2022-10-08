package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public Collection<ItemDto> searchItems(@RequestParam String text,
                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                   @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен GET запрос к эндпоинту /items/search?text={}", text);
        final PageRequest pageRequest = findPageRequest(from, size);
        return ItemMapper.toItemDtoCollection(itemService.searchItems(text, pageRequest));
    }

    @GetMapping
    public Collection<ItemByIdDto> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId,
                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                   @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен GET запрос к эндпоинту /items");
        final PageRequest pageRequest = findPageRequest(from, size);
        return itemService.getAllItemsByUser(userId, pageRequest);
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

    public PageRequest findPageRequest(Integer from, Integer size) {
        validateParam(from, size);
        int page = from / size;
        return PageRequest.of(page, size);
    }

    public void validateParam(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр from не может быть < 0.");
        }
        if (size < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр size не может быть < 0.");
        }
        if (from == 0 && size == 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр from и size не могут одновременно быть = 0.");
        }
    }
}
