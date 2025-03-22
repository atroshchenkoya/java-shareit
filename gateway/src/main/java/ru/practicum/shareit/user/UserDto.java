package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.validation.groups.Create;
import ru.practicum.shareit.util.validation.groups.Update;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;
}
