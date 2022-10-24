package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequestMapping(path = "/items")
@RestController
@RequiredArgsConstructor
@Validated
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                @PathVariable Long itemId,
                                @Validated({Update.class}) @RequestBody CommentDtoIn commentDtoIn) {
        return itemClient.createComment(authorId, itemId, commentDtoIn);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен GET запрос к эндпоинту /items/search?text={}", text);
        return itemClient.searchItems(text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен GET запрос к эндпоинту /items");
        return itemClient.getAllItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                  @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) Long userId,
                           @PathVariable Long itemId) {
        itemClient.deleteItem(itemId);
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
