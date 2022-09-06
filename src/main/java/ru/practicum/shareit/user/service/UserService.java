package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    User getUserById(Long userId);

    Collection<User> getAllUsers();

    void deleteUser(Long userId);
}
