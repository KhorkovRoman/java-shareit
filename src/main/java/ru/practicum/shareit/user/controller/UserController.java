package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    Collection<UserDto> getAllUsers() {
        return UserMapper.toUserDtoCollection(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable Integer userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @PostMapping
    UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.createUser(userDto));
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@PathVariable Integer userId,
                           @Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

}
