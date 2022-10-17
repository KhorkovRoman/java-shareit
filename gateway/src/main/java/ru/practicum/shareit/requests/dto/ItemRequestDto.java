package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private UserDto requester;
    @NotNull(groups = {Create.class})
    private LocalDateTime created;
}
