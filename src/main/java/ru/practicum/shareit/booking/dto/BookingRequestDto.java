package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    @NotNull(groups = Create.class, message = "Item ID cannot be null")
    private Long itemId;

    @FutureOrPresent(groups = Create.class, message = "Start time must be in future or present")
    @NotNull(groups = Create.class, message = "Start time cannot be null")
    private LocalDateTime start;

    @Future(groups = Create.class, message = "End time must be in future")
    @NotNull(groups = Create.class, message = "End time cannot be null")
    private LocalDateTime end;
}