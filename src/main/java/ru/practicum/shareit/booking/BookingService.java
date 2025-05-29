package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto setBookingApproval(long userId, long bookingId, boolean approved);

    BookingDto createBooking(long userId, BookingRequestDto bookingDto);

    BookingDto getBookingByIdAndBookerOrOwner(long bookingId, long userId);

    Booking getBookingById(long bookingId);

    List<BookingDto> findBookingsByItemOwner(long userId);

    List<BookingDto> findBookingsByBookerId(long userId);

    boolean existsBookingByBookerIdOrItemOwner(long bookerId, long ownerId);

    List<BookingDto> findBookingsByBookerIdOrItemOwner(long bookerId, long ownerId);

    List<BookingDto> findBookingsByBookerIdAndStatusWaiting(long userId);

    List<BookingDto> findBookingsByItemOwnerAndStatusWaiting(long userId);

    List<BookingDto> findBookingsByItemOwnerAndStatusRejected(long userId);

    List<BookingDto> findBookingsByBookerIdAndStatusRejected(long userId);

    List<BookingDto> findBookingsByStateAndOwnerId(long userId, String state);

    List<BookingDto> findBookingsByStateAndBookerId(long userId, String state);


}
