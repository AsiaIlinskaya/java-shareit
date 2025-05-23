package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    @FutureOrPresent(groups = Create.class, message = "Время начала бронирования должно быть в будущем")
    @Column(name = "start_date")
    private LocalDateTime start;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    @Future(groups = Create.class, message = "Время окончания бронирования должно быть в будущем")
    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @NotNull(groups = Create.class, message = "Предмет не может быть пустым")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @NotNull(groups = Create.class, message = "Бронирующий не может быть пустым")
    @JoinColumn(name = "booker_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User booker;

    @NotNull(groups = Create.class, message = "Статус не может быть пустым")
    @Column(name = "status")
    private BookingStatus status;
}

