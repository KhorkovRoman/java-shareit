package ru.practicum.shareit.user.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Component
public class ValidationUser {
    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Электронная почта не может быть пустой и  должна содежать символ @.");
        }

    }
}
