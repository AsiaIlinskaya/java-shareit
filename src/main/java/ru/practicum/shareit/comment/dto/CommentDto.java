package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.intf.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(groups = Create.class, message = "Текст не может быть пустым")
    private String text;

    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String authorName;

    @NotNull(message = "Дата создания комментария не может быть пустой")
    private LocalDateTime created;
}
