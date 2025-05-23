package ru.practicum.shareit.comment.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Column(name = "text")
    private String text;

    @NotNull(message = "ID предмета не может быть пустым")
    @Column(name = "item_id")
    private Long itemId;

    @NotNull(message = "ID автора не может быть пустым")
    @Column(name = "author_id")
    private Long authorId;

    @NotNull(message = "Дата создания комментария не может быть пустой")
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}