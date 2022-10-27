package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exeption.UnknownStateException;

import java.util.Optional;

public enum BookingState {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static Optional<BookingState> from(String stringState) {
		boolean checkState = false;
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				checkState = true;
				return Optional.of(state);
			}
		}
		if (!checkState) {
			throw new UnknownStateException("Unknown state: " + stringState);
		}
		return Optional.empty();
	}
}
