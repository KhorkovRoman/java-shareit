package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ItemRequestByIdDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private LocalDateTime created;
    @NotNull(groups = {Create.class})
    private Collection<ItemDto> items;
}
