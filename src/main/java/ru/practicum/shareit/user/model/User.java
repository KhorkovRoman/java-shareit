package ru.practicum.shareit.user.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {
    private int id;
    private String name;
    private String email;
}
