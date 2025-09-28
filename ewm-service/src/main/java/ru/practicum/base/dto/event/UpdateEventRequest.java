package ru.practicum.base.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.base.dto.location.LocationDto;
import ru.practicum.base.util.notblanknull.NotBlankNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class UpdateEventRequest {

    @Length(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
    @NotBlankNull(message = "Аннотация не может быть пустой")
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
    @NotBlankNull(message = "Описание не может быть пустым")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero(message = "Лимит участников должен быть больше 0")
    private Long participantLimit;
    private Boolean requestModeration;
    @Length(min = 3, max = 120, message = "Длина названия должна быть от 3 до 120 символов")
    @NotBlankNull(message = "Описание не может быть пустым")
    private String title;

}
