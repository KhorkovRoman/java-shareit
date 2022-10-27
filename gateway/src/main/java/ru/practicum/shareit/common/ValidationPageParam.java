package ru.practicum.shareit.common;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exeption.ValidationException;

public class ValidationPageParam {

    Integer from;
    Integer size;

    public ValidationPageParam(Integer from, Integer size) {
        this.from = from;
        this.size = size;
    }

    public void validatePageParam() {
        if (from < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр from не может быть < 0.");
        }
        if (size < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр size не может быть < 0.");
        }
        if (from == 0 && size == 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    "Параметр from и size не могут одновременно быть = 0.");
        }
    }
}
