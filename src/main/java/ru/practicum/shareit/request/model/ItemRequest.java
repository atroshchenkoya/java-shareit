package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder(toBuilder = true)
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private Long timestamp;
}
