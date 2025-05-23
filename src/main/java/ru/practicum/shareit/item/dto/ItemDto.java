package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.intf.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = Create.class, message = "Описание не может быть пустым")
    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

}
