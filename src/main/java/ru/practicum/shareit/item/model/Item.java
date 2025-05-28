package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.intf.Create;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    @Column(name = "name")
    private String name;

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    @Column(name = "description")
    private String description;

    @NotNull(groups = Create.class, message = "Не может быть пустым")
    @Column(name = "is_available")
    private Boolean available;

    @JsonIgnore
    @Column(name = "owner_id")
    private long owner;

    @JsonIgnore
    @Column(name = "request_id")
    private long request;

    @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL)
    private List<Comment> comments;
}
