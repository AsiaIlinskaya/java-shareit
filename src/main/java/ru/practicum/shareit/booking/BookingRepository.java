package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingDto;

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

    boolean existsByIdAndBookerIdOrItemOwner(Long id, Long bookerId, Long ownerId);

    List<Booking> findBookingsByBookerIdOrderByStartDesc(long userId);

    List<Booking> findBookingsByBookerIdAndStatus(long userId, BookingStatus bookingStatus, Sort start);

    List<Booking> findBookingsByItemOwnerAndStatus(long userId, BookingStatus bookingStatus, Sort start);

    List<BookingDto> findBookingsByItemOwnerAndStartBeforeAndEndAfter(long userId, LocalDateTime now, LocalDateTime now1);

    List<BookingDto> findBookingsByBookerIdAndStartBeforeAndEndAfter(long userId, LocalDateTime now, LocalDateTime now1);

    List<BookingDto> findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(long userId, LocalDateTime now);

    List<BookingDto> findBookingsByBookerIdAndEndBeforeOrderByEndDesc(long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = ru.practicum.shareit.booking.BookingStatus.APPROVED " +
            "ORDER BY b.start ASC")
    List<Booking> findApprovedBookingsForItems(@Param("itemIds") List<Long> itemIds);
}








