package ru.practicum.shareit.booking;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ValidationException;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(
                                HttpClients.custom()
                                        .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                                                .build())
                                        .build()))
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, String state, Integer from, Integer size) {
        return get("?state=" + state + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        if (requestDto.getStart().isAfter(requestDto.getEnd())) {
            throw new ValidationException("Booking start time cannot be after end time");
        }

        if (requestDto.getStart().isEqual(requestDto.getEnd())) {
            throw new ValidationException("Booking start time cannot be equal to booking end time");
        }
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> setBookingApproval(long userId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> findBookingsByStateAndOwnerId(long userId, String state, int from, int size) {
        return get("/owner?state=" + state + "&from=" + from + "&size=" + size, userId);
    }
}
