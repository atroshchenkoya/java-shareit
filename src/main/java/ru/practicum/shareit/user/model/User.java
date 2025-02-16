package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.util.validation.groups.Create;
import ru.practicum.shareit.util.validation.groups.Update;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;
}
