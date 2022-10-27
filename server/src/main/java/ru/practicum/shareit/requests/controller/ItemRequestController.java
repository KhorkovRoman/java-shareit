package ru.practicum.shareit.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestByIdDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.createItemRequest(userId, itemRequestDtoIn));
    }

    @GetMapping("/{requestId}")
    public ItemRequestByIdDto getItemRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @GetMapping
    public Collection<ItemRequestByIdDto> getAllItemRequestsByRequester(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllItemRequestsByRequester(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestByIdDto> getAllItemRequestsByPage(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получен Get запрос к эндпоинту /requests/all?from={}size={}", from, size);
        final PageRequest pageRequest = findPageRequest(from, size);
        return itemRequestService.getAllItemRequestsByPage(userId, pageRequest);
    }

    public PageRequest findPageRequest(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
