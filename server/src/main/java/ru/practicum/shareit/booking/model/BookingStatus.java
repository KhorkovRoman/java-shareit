package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exeption.UnknownStateException;

import java.util.Objects;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static State findState(String stateText) {
        boolean checkState = false;
        for (State state: State.values()) {
            if (Objects.equals(state.toString(), stateText)) {
                checkState = true;
                break;
            }
        }
        if (!checkState) {
            throw new UnknownStateException("Unknown state: " + stateText);
        }
        return State.valueOf(stateText);
    }
}
