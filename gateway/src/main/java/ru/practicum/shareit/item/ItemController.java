package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.common.ValidationPageParam;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@SuppressWarnings("ALL")
@Slf4j
@RequestMapping(path = "/items")
@RestController
@RequiredArgsConstructor
@Validated
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private ValidationPageParam validationPageParam;

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                  @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Request to endpoint POST/items от userId={}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                @PathVariable Long itemId,
                                @Validated({Update.class}) @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Request to endpoint GET/items/{}/comment от userId={}", itemId, authorId);
        return itemClient.createComment(authorId, itemId, commentDtoIn);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "20") Integer size) {
        validatePage(from, size);
        log.info("Request to endpoint GET/items/search?text={}", text);
        return itemClient.searchItems(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Request to endpoint GET/items/{} от userId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        validatePage(from, size);
        log.info("Request to endpoint GET/items/ от userId={}", userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                  @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Request to endpoint PATCH/items/{} от userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) Long userId,
                           @PathVariable Long itemId) {
        log.info("Request to endpoint DELETE/items/{} от userId={}", itemId, userId);
        itemClient.deleteItem(itemId);
    }

    private void validatePage(Integer from, Integer size) {
        validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}
