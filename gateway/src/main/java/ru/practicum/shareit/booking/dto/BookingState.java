package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
	ALL,
	CURRENT,
	FUTURE,
	PAST,
	REJECTED,
	WAITING;

	public static Optional<BookingState> from(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}

	public static boolean isValid(String state) {
		for (BookingState bookingState : BookingState.values()) {
			if (bookingState.name().equalsIgnoreCase(state)) {
				return true;
			}
		}
		return false;
	}
}
