package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.intf.Create;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
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

    @NotNull(groups = Create.class, message = "Описание не может быть пустым")
    @Column(name = "is_available")
    private Boolean available;

    @Column(name = "owner_id")
    private long owner;

    @Column(name = "request_id")
    private long request;

    @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL)
    private List<Comment> comments;

}
