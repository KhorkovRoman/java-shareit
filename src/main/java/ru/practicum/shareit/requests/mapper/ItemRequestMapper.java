package ru.practicum.shareit.requests.mapper;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestByIdDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoIn;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Service
public class ItemRequestMapper {

    public static ItemRequestByIdDto toItemRequestByIdDto(ItemRequest itemRequest,
                                                          Collection<ItemDto> itemDtoList) {
        return ItemRequestByIdDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtoList)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(Long itemRequestId, User user,
                                            ItemRequestDtoIn itemRequestDtoIn, LocalDateTime dateTimeNow) {
        return ItemRequest.builder()
                .id(itemRequestId)
                .description(itemRequestDtoIn.getDescription())
                .requester(user)
                .created(dateTimeNow)
                .build();
    }
}
