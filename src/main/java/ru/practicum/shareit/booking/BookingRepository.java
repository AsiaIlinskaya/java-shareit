package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByItemOwner(long ownerId, Sort sort);

    boolean existsBookingsByBookerIdOrItemOwner(long bookerId, long ownerId);

    List<Booking> findBookingsByBookerIdOrItemOwner(long bookerId, long ownerId, Sort sort);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status,
                                                           LocalDateTime localDateTime);

    Booking findFirstBookingByItemIdAndStatusAndStartIsBefore(long itemId, BookingStatus bookingStatus,
                                                              LocalDateTime now, Sort start);

    Booking findFirstBookingByItemIdAndStatusAndStartIsAfter(long itemId, BookingStatus bookingStatus,
                                                             LocalDateTime now, Sort start);

    List<Booking> findBookingsByBookerIdOrderByStartDesc(long userId);

    List<Booking> findBookingsByBookerIdAndStatus(long userId, BookingStatus bookingStatus, Sort start);

    List<Booking> findBookingsByItemOwnerAndStatus(long userId, BookingStatus bookingStatus, Sort start);

    List<Booking> findBookingsByItemOwnerAndStartBeforeAndEndAfter(long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findBookingsByBookerIdAndStartBeforeAndEndAfter(long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(long userId, LocalDateTime now);

    List<Booking> findBookingsByBookerIdAndEndBeforeOrderByEndDesc(long userId, LocalDateTime now);
}








