package ru.practicum.shareit.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(message = "Текст не может быть пустым")
    private String text;

    @NotBlank(message = "Имя не может быть пустым")
    private String authorName;

    @NotNull(message = "Дата создания комментария не может быть пустой")
    private LocalDateTime created;
}
