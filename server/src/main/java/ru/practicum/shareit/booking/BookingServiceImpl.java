package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto setBookingApproval(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        if (booking.getItem().getOwner() != userId) {
            throw new ValidationException("User with ID " + userId + " is not the owner of the item.");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking with ID " + bookingId +
                    " cannot be modified because it's not in WAITING status.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + bookingRequestDto.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("The item is not available for booking.");
        }

        if (userId == item.getOwner()) {
            throw new ResourceNotFoundException("User cannot book own item");
        }

        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = repository.save(booking);
        return bookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto getBookingByIdAndBookerOrOwner(long bookingId, long userId) {
        Booking booking = getBookingById(bookingId);

        if (!repository.existsByIdAndBookerIdOrItemOwner(bookingId, userId, userId)) {
            throw new ResourceNotFoundException("Booking not found for user with ID: " + userId);
        }

        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Booking getBookingById(long bookingId) {
        return repository.findById(bookingId).orElseThrow(()
                -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
    }

    @Override
    public List<BookingDto> findBookingsByBookerId(long userId) {
        List<Booking> bookings = repository.findBookingsByBookerIdOrderByStartDesc(userId);

        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found with Booker ID: " + userId);
        }
        return bookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findBookingsByStateAndOwnerId(long userId, String state) {
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

    private List<BookingDto> findPastBookingsByOwnerId(long userId) {
        return repository.findBookingsByItemOwnerAndEndBeforeOrderByEndDesc(userId, LocalDateTime.now());
    }

    private List<BookingDto> findPastBookingsByBookerId(long userId) {
        return repository.findBookingsByBookerIdAndEndBeforeOrderByEndDesc(userId, LocalDateTime.now());
    }

    private List<BookingDto> findCurrentBookingsByOwnerId(long userId) {
        return repository.findBookingsByItemOwnerAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
    }

    private List<BookingDto> findCurrentBookingsByBookerId(long userId) {
        return repository.findBookingsByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
    }

    @Override
    public List<BookingDto> findBookingsByStateAndBookerId(long userId, String state) {
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
    public List<BookingDto> findBookingsByItemOwner(long userId) {
        List<Booking> bookings = repository.findBookingsByItemOwner(userId,
                Sort.by(Sort.Direction.DESC, "start"));
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found with Owner ID: " + userId);
        }

        return bookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findBookingsByBookerIdOrItemOwner(long bookerId, long ownerId) {
        List<Booking> bookings = repository.findBookingsByBookerIdOrItemOwner(bookerId, ownerId,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findBookingsByBookerIdAndStatusWaiting(long userId) {
        List<Booking> bookings = repository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findBookingsByItemOwnerAndStatusWaiting(long userId) {
        List<Booking> bookings = repository.findBookingsByItemOwnerAndStatus(userId, BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findBookingsByItemOwnerAndStatusRejected(long userId) {
        List<Booking> bookings = repository.findBookingsByItemOwnerAndStatus(userId, BookingStatus.REJECTED,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookingMapper.mapToBookingDtoList(bookings);
    }

    @Override
    public List<BookingDto> findBookingsByBookerIdAndStatusRejected(long userId) {
        List<Booking> bookings = repository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                Sort.by(Sort.Direction.DESC, "start"));
        return bookingMapper.mapToBookingDtoList(bookings);
    }
}
