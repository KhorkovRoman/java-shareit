package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestByIdDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestService(ItemRequestRepository itemRequestRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private Long itemRequestId = 0L;

    public Long generateItemRequestId() {
        return ++itemRequestId;
    }

    @SuppressWarnings("checkstyle:Regexp")
    public ItemRequest createItemRequest(Long userId, ItemRequestDtoIn itemRequestDtoIn) {
        User user = getUser(userId);
        validateUser(user, userId);
        validateItemRequest(itemRequestDtoIn);
        LocalDateTime dateTimeNow = LocalDateTime.now();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(generateItemRequestId(), user,
                itemRequestDtoIn, dateTimeNow);
        return itemRequestRepository.save(itemRequest);
    }

    public ItemRequestByIdDto getItemRequestById(Long requestId, Long userId) {
        User user = getUser(userId);
        validateUser(user, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Не найден запрос c id " + requestId));

        Collection<ItemDto> itemDtoList = ItemMapper.toItemDtoCollection(itemRepository.getAllItemsByRequest(requestId));

        return ItemRequestMapper.toItemRequestByIdDto(itemRequest, itemDtoList);
    }

    public Collection<ItemRequestByIdDto> getAllItemRequestsByRequester(Long userId) {
        User user = getUser(userId);
        validateUser(user, userId);
        Collection<ItemRequest> itemRequestCollection = itemRequestRepository.getAllItemRequestsByRequester(userId);
        return itemRequestCollection.stream()
                .map(itemRequest -> getItemRequestById(itemRequest.getId(), userId))
                .collect(Collectors.toList());
    }

    public Collection<ItemRequestByIdDto> getAllItemRequestsByPage(Long userId, PageRequest pageRequest) {
        User user = getUser(userId);
        validateUser(user, userId);
        Page<ItemRequest> itemRequestCollection = itemRequestRepository.findAllItemRequests(userId, pageRequest);
        return itemRequestCollection.stream()
                .map(itemRequest -> getItemRequestById(itemRequest.getId(), userId))
                .collect(Collectors.toList());
    }

    public void validateItemRequest(ItemRequestDtoIn itemRequestDtoIn) {
        if (itemRequestDtoIn.getDescription() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Не указано описание.");
        }
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id " + userId));
    }

    public void validateUser(User user, Long userId) {
        if (userId == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Id пользователя не указан.");
        }
        if (user == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "В базе нет пользователя c id " + userId);
        }
    }
}
