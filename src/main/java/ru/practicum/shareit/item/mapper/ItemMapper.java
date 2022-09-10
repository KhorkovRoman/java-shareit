package ru.practicum.shareit.item.mapper;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Service
public class ItemMapper {
    public static Collection<ItemDto> toItemDtoCollection(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static CommentDtoOut toCommentDto(Comment comment) {
        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Collection<CommentDtoOut> toCommentDtoOutCollection(Collection<Comment> comments) {
        return comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static ItemByIdDto toItemByIdDto(Item item, BookingDto lastBookingId, BookingDto nextBookingId,
                                            Collection<CommentDtoOut> commentList) {
        return new ItemByIdDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingId,
                nextBookingId,
                commentList
        );
    }

    public static Item toItem(Long itemId, User owner, ItemDto itemDto) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }
}
