package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.intf.Create;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = Create.class, message = "Requestor не может быть равен null")
    private Long requestor;

    @NotNull(groups = Create.class, message = " Время не может быть пустым")
    private Timestamp created;
}
