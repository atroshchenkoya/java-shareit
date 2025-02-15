package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.validation.groups.Create;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDto {
    @EqualsAndHashCode.Include
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
