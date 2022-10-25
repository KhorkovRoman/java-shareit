package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemByIdDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    @NotNull(groups = {Create.class})
    private BookingDtoItem lastBooking;
    @NotNull(groups = {Create.class})
    private BookingDtoItem nextBooking;
    @NotNull(groups = {Create.class})
    private Collection<CommentDtoOut> comments;
}
