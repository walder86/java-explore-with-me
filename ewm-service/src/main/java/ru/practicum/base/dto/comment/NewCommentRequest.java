package ru.practicum.base.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentRequest {

    @NotBlank
    @Size(min = 3, max = 7000, message = "Комментарий должен содержать от 3 до 7000 тысяч символов")
    private String text;
    @NotNull(message = "ID события не может быть пустым")
    private Long event;
}
