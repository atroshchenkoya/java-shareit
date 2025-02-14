package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requester;
    private Long timestamp;
}
