package ru.practicum.base.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя должно быть от 2 до 250 символов")
    private String name;
    @Email(message = "Почта должна быть вида email@email.ru")
    @NotBlank(message = "Почта не может быть пустой")
    @Size(min = 6, max = 254, message = "Длина почты должна быть от 6 до 254 символов")
    private String email;
}
