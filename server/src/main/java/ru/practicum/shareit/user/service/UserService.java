package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void deleteUser(Long userId);
}
