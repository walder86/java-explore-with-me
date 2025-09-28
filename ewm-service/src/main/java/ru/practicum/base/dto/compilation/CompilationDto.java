package ru.practicum.base.dto.compilation;

import lombok.*;
import ru.practicum.base.dto.event.EventShortDto;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private List<EventShortDto> events;
    private Long id;
    private boolean pinned;
    private String title;
}
