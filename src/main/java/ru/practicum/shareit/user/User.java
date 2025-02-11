package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String email;
}
