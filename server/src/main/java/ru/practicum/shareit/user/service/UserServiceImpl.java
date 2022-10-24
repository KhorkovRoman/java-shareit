package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.ValidationUser;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUser validationUser;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ValidationUser validationUser) {
        this.userRepository = userRepository;
        this.validationUser = validationUser;
    }

    private Long userId = 0L;

    private Long generateUserId() {
        return ++userId;
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validationUser.validateUserEmail(user);
        validateUserByEmail(user);
        user.setId(generateUserId());
        log.info("Пользователь с id " + user.getId() + " успешно создан.");
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);

        validateUser(userId);
        if (user.getEmail() == null) {
            user.setEmail(getUserById(userId).getEmail());
        }
        validateUserByEmail(user);
        validationUser.validateUserEmail(user);

        if (user.getName() == null) {
            user.setName(getUserById(userId).getName());
        }

        log.info("Пользователь с id " + user.getId() + " успешно обновлен.");
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "В базе нет пользователя c id " + userId));
        log.info("Пользователь c id " + userId + " найден в базе.");
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId) {
        //validateUser(userId);
        userRepository.deleteById(userId);
    }

    public void validateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND,
                        "Пользователя c id " + userId + " нет в базе."));
    }

    public void validateUserByEmail(User user) {
        if (userRepository.findAll().contains(user.getEmail())) {
                throw new ValidationException(HttpStatus.CONFLICT, "Пользователь c e-mail " + user.getEmail()
                        + " уже есть в базе .");
        }
    }
}
