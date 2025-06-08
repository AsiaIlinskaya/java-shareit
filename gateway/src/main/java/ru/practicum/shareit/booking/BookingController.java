package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.intf.Create;

import java.util.Map;

import static ru.practicum.shareit.item.ItemController.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (!BookingState.isValid(state)) {
            String errorMessage = String.format("Unknown state: %s", state);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID) long userId,
                                           @RequestBody @Validated(Create.class) BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> setBookingApproval(@RequestHeader(USER_ID) long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam("approved") boolean approved) {
        log.info("Updating booking status for bookingId: {}, userId: {}, approved: {}", bookingId, userId, approved);
        return bookingClient.setBookingApproval(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findBookingsByStateAndOwnerId(@RequestHeader(USER_ID) long userId,
                                                                @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                                @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                                @Positive @RequestParam(value = "size", defaultValue = "20") int size) {
        if (!BookingState.isValid(state)) {
            String errorMessage = String.format("Unknown state: %s", state);
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
        }
        log.info("Get bookings by owner with ID: {} and state: {}, from: {}, size: {}", userId, state, from, size);
        return bookingClient.findBookingsByStateAndOwnerId(userId, state, from, size);
    }
}
