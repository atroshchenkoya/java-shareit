package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.validation.groups.Create;

import java.time.LocalDateTime;
import java.util.List;

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

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ItemRequest request;

    private LocalDateTime lastBooking;

    private LocalDateTime nextBooking;

    private List<CommentDto> comments;
}
