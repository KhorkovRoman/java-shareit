package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.ValidationPageParam;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@SuppressWarnings("ALL")
@Slf4j
@RequestMapping(path = "/requests")
@RestController
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private ValidationPageParam validationPageParam;

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                         @Validated({Create.class}) @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Request to endpoint POST/requests from userId={}", userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDtoIn);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Request to endpoint GET/requests/{} from userId={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByRequester(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Request to endpoint GET/requests from userId={}", userId);
        return itemRequestClient.getAllItemRequestsByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsByPage(
                            @RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        validatePage(from, size);
        log.info("Request to endpoint GET/requests/all?from={}&size={} from userId={}", from, size, userId);
        return itemRequestClient.getAllItemRequestsByPage(userId, from, size);
    }

    private void validatePage(Integer from, Integer size) {
        validationPageParam = new ValidationPageParam(from, size);
        validationPageParam.validatePageParam();
    }
}
