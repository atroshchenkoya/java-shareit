package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса не должно быть пустым")
    private String description;

    private LocalDateTime created;
    private List<ItemDto> items;
}