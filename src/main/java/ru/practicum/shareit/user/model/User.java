package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.validation.groups.Create;
import ru.practicum.shareit.util.validation.groups.Update;

@Data
@Builder(toBuilder = true)
public class User {
    private Long id;

    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;
}
