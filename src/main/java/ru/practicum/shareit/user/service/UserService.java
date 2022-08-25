package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.ValidationUser;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final ValidationUser validationUser;

    @Autowired
    public UserService(UserStorage userStorage, ValidationUser validationUser) {
        this.userStorage = userStorage;
        this.validationUser = validationUser;
    }

    private int userId = 0;

    public int generateUserId() {
        return ++userId;
    }

    public User createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validationUser.validateUser(user);
        validateUserEmail(user);
        user.setId(generateUserId());
        log.info("Пользователь с id " + user.getId() + " успешно создан.");
        return userStorage.createUser(user);
    }

    public User updateUser(Integer userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);

        validateUser(userId);
        if (user.getEmail() == null) {
            user.setEmail(getUserById(userId).getEmail());
        }
        String oldEmail = userStorage.getUserById(userId).getEmail();
        userStorage.getSetEmail().remove(oldEmail);

        validateUserEmail(user);
        validationUser.validateUser(user);

        if (user.getName() == null) {
            user.setName(getUserById(userId).getName());
        }

        log.info("Пользователь с id " + user.getId() + " успешно обновлен.");
        return userStorage.updateUser(user);
    }

    public User getUserById(Integer userId) {
        validateUser(userId);
        User user = userStorage.getUserById(userId);
        log.info("Пользователь c id " + userId + " найден в базе.");
        return user;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(Integer userId) {
        validateUser(userId);
        userStorage.deleteUser(userId);
    }

    public void validateUser(Integer userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND,
                    "Пользователя c id " + userId + " нет в базе.");
        }
    }

    public void validateUserEmail(User user) {
        if (userStorage.getSetEmail().contains(user.getEmail())) {
                throw new ValidationException(HttpStatus.CONFLICT, "Пользователь c e-mail " + user.getEmail()
                        + " уже есть в базе .");
        } else {
            userStorage.getSetEmail().add(user.getEmail());
            log.info("Email " + user.getEmail() + " добавлен в базу.");
        }
    }
}
