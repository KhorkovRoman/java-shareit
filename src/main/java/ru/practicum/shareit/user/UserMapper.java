package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class UserMapper {

    public static Collection<UserDto> toUserDtoCollection(Collection<User> users) {
        Collection<UserDto> usersDto = new ArrayList<>();

        for (User user: users) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}


