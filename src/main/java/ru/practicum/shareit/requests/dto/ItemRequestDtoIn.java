package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ItemRequestDtoIn {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
}
