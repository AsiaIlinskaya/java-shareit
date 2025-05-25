package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking setBookingApproval(long userId, long bookingId, boolean approved) {
        Booking booking = repository.findById(bookingId).orElseThrow(()
                -> new ResourceNotFoundException("Booking with ID " + bookingId + " is not found."));

        if (booking.getItem().getOwner() != userId) {
            throw new ValidationException("User with ID " + userId + " is not the owner of the item.");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Booking with ID " + bookingId + " has already been approved.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return booking;
    }

    @Override
    @Transactional
    public Booking createBooking(long userId, BookingDto bookingDto) {
        if (bookingDto.getStart() == null) {
            throw new ValidationException("Booking start time cannot be null");
        }

        if (bookingDto.getEnd() == null) {
            throw new ValidationException("Booking end time cannot be null");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new ResourceNotFoundException("Item not found with ID: " + userId);
        }

        if (itemRepository.getById(bookingDto.getItemId()).getAvailable() == false) {
            throw new ValidationException("The item is not available for booking.");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Booking start time cannot be in the past");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Booking end time cannot be before booking start time");
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Booking start time cannot be equal to booking end time");
        }

        long ownerId = itemRepository.getReferenceById(bookingDto.getItemId()).getOwner();
        if (userId == ownerId) {
            throw new ResourceNotFoundException("User cannot book own item");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(userRepository.getReferenceById(userId));
        booking.setItem(itemRepository.getReferenceById(bookingDto.getItemId()));
        booking.setStatus(BookingStatus.WAITING);
        repository.save(booking);
        return booking;
    }

    @Override
    public Booking getBookingByIdAndBookerOrOwner(long bookingId, long userId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getItem().getOwner() == userId) {
            return booking;
        }

        if (booking.getBooker().getId() == userId) {
            return booking;
        }

        throw new ResourceNotFoundException("Booking not found for user with ID: " + userId);
    }

    @Override
    public Booking getBookingById(long bookingId) {
        return repository.findById(bookingId).orElseThrow(()
                -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
    }

    @Override
    public List<Booking> findBookingsByBookerId(long userId) {
        List<Booking> bookings = repository.findBookingsByBookerIdOrderByStartDesc(userId);

        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found with Booker ID: " + userId);
        }
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByStateAndOwnerId(long userId, String state) {
        if (!existsBookingByBookerIdOrItemOwner(userId, userId)) {
            throw new ResourceNotFoundException("No bookings found for user with ID: " + userId);
        }

        if (state != null) {
            switch (state) {
                case "ALL":
                    log.info("Retrieving all bookings for owner with ID: {}", userId);
                    return findBookingsByItemOwner(userId);
                case "FUTURE":
                    log.info("Retrieving future bookings for owner with ID: {}", userId);
                    return findBookingsByItemOwner(userId);
                case "WAITING":
                    log.info("Retrieving waiting bookings for owner with ID: {}", userId);
                    return findBookingsByItemOwnerAndStatusWaiting(userId);
                case "REJECTED":
                    log.info("Retrieving rejected bookings for owner with ID: {}", userId);
                    return findBookingsByItemOwnerAndStatusRejected(userId);
                case "CURRENT":
                    log.info("Retrieving current bookings for owner with ID: {}", userId);
                    return findCurrentBookingsByOwnerId(userId);
                case "PAST":
                    log.info("Retrieving past bookings for owner with ID: {}", userId);
                    return findPastBookingsByOwnerId(userId);
                default:
                    throw new ValidationException("Unknown state: " + state);
            }
        } else {
            log.info("No state specified. Retrieving all bookings for owner with ID: {}", userId);
            return findBookingsByBookerIdOrItemOwner(userId, userId);
        }
    }

    private List<Booking> findPastBookingsByOwnerId(long userId) {
        return repository.findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(userId, LocalDateTime.now());
    }

    private List<Booking> findPastBookingsByBookerId(long userId) {
        return repository.findBookingsByBookerIdAndEndBeforeOrderByEndDesc(userId, LocalDateTime.now());
    }

    private List<Booking> findCurrentBookingsByOwnerId(long userId) {
        return repository.findBookingsByItemOwnerAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
    }

    private List<Booking> findCurrentBookingsByBookerId(long userId) {
        return repository.findBookingsByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
    }

    @Override
    public List<Booking> findBookingsByStateAndBookerId(long userId, String state) {
        if (state != null) {
            switch (state) {
                case "ALL":
                    log.info("Retrieving all bookings for booker with ID: {}", userId);
                    return findBookingsByBookerId(userId);
                case "FUTURE":
                    log.info("Retrieving future bookings for booker with ID: {}", userId);
                    return findBookingsByBookerId(userId);
                case "WAITING":
                    log.info("Retrieving waiting bookings for booker with ID: {}", userId);
                    return findBookingsByBookerIdAndStatusWaiting(userId);
                case "REJECTED":
                    log.info("Retrieving rejected bookings for booker with ID: {}", userId);
                    return findBookingsByBookerIdAndStatusRejected(userId);
                case "PAST":
                    log.info("Retrieving past bookings for booker with ID: {}", userId);
                    return findPastBookingsByBookerId(userId);
                case "CURRENT":
                    log.info("Retrieving current bookings for booker with ID: {}", userId);
                    return findCurrentBookingsByBookerId(userId);
                default:
                    throw new ValidationException("Unknown state: " + state);
            }
        } else {
            log.info("No state specified. Retrieving all bookings for booker with ID: {}", userId);
            return findBookingsByBookerIdOrItemOwner(userId, userId);
        }
    }

    @Override
    public boolean existsBookingByBookerIdOrItemOwner(long bookerId, long ownerId) {
        return repository.existsBookingsByBookerIdOrItemOwner(bookerId, ownerId);
    }

    @Override
    public List<Booking> findBookingsByItemOwner(long userId) {
        List<Booking> bookings = repository.findBookingsByItemOwner(userId,
                Sort.by(Sort.Direction.DESC, "start"));
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found with Owner ID: " + userId);
        }

        return bookings;
    }

    @Override
    public List<Booking> findBookingsByBookerIdOrItemOwner(long bookerId, long ownerId) {
        List<Booking> bookings = repository.findBookingsByBookerIdOrItemOwner(bookerId, ownerId,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByBookerIdAndStatusWaiting(long userId) {
        List<Booking> bookings = repository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByItemOwnerAndStatusWaiting(long userId) {
        List<Booking> bookings = repository.findBookingsByItemOwnerAndStatus(userId, BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByItemOwnerAndStatusRejected(long userId) {
        List<Booking> bookings = repository.findBookingsByItemOwnerAndStatus(userId, BookingStatus.REJECTED,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookings;
    }

    @Override
    public List<Booking> findBookingsByBookerIdAndStatusRejected(long userId) {
        List<Booking> bookings = repository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookings;
    }
}
