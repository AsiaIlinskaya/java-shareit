package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    private final ItemMapper itemMapper = new ItemMapper();
    private final UserMapper userMapper = new UserMapper();

    public BookingDto mapToBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Booking mapToBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }

        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus() != null ? bookingDto.getStatus() : BookingStatus.WAITING)
                .build();
    }

    public List<BookingDto> mapToBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());
    }
}