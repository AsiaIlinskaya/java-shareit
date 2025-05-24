package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.intf.Create;

import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestHeader(USER_ID) long userId,
                                 @RequestBody @Validated(Create.class) BookingDto bookingDto) {
        log.info("Received request to save new booking for user with ID: {}", userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking setBookingApproval(@RequestHeader(USER_ID) long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam("approved") boolean approved) {
        log.info("Received request to update booking status for user with ID: {}", userId);
        return bookingService.setBookingApproval(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking getBookingByIdAndBookerOrOwner(@PathVariable long bookingId, @RequestHeader(USER_ID) long userId) {
        log.info("Received request to get booking by ID {} for user with ID: {}", bookingId, userId);
        return bookingService.getBookingByIdAndBookerOrOwner(bookingId, userId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> findBookingsByBookerId(@RequestHeader(USER_ID) long userId) {
        log.info("Received request to get bookings by booker with ID: {}", userId);
        return bookingService.findBookingsByBookerId(userId);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> findBookingsByStateAndOwnerId(@RequestHeader(USER_ID) long userId,
                                                       @RequestParam(value = "state", required = false) String state) {
        log.info("Received request to get bookings by owner with ID: {} and state: {}", userId, state);
        return bookingService.findBookingsByStateAndOwnerId(userId, state);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> findBookingsByStateAndBookerId(@RequestHeader(USER_ID) long userId,
                                                        @RequestParam(value = "state", required = false) String state) {
        log.info("Received request to get bookings by booker with ID: {} and state: {}", userId, state);
        return bookingService.findBookingsByStateAndBookerId(userId, state);
    }
}


