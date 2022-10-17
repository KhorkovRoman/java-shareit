package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoOut {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String text;
    @NotBlank(groups = {Create.class})
    private String authorName;
    @NotNull(groups = {Create.class})
    private LocalDateTime created;
}