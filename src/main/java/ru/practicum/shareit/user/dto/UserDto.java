package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDto {
    private int id;
    private String name;
    private String email;
}
