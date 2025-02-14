package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.validation.groups.Create;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не должно быть пустым.", groups = Create.class)
    private String name;

    @NotBlank(message = "Описание не должно быть пустым.", groups = Create.class)
    private String description;

    @NotNull(message = "Поле доступности не должно быть пустым.", groups = Create.class)
    private Boolean available;

    private User owner;
    private ItemRequest request;
}
