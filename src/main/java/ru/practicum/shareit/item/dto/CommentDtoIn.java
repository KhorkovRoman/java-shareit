package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDtoIn {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String text;
}
