package ru.practicum.base.dto.comment;

import lombok.*;
import ru.practicum.base.dto.event.EventShortDto;
import ru.practicum.base.dto.user.UserShortDto;
import ru.practicum.base.enums.StatusComment;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private LocalDateTime created;
    private EventShortDto event;
    private UserShortDto commentator;
    private StatusComment status;
}
