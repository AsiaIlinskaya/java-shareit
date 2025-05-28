package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.intf.Create;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private long id;

    @FutureOrPresent(groups = Create.class, message = "Время начала бронирования должно быть в будущем")
    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    private LocalDateTime start;

    @Future(groups = Create.class, message = "Время окончания бронирования должно быть в будущем")
    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    private LocalDateTime end;

    @NotNull(groups = Create.class, message = "Предмет не может быть пустым")
    private long itemId;

    private long bookerId;

    private BookingStatus status;
}
