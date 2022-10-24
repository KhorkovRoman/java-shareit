package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequestMapping(path = "/requests")
@RestController
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                         @Validated({Create.class}) @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestClient.createItemRequest(userId, itemRequestDtoIn);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByRequester(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getAllItemRequestsByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsByPage(
                            @RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен Get запрос к эндпоинту /requests/all?from={}size={}", from, size);
        return itemRequestClient.getAllItemRequestsByPage(userId, from, size);
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
